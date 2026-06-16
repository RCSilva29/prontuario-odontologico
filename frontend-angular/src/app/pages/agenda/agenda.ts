import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { Consulta, ConsultaRequest } from '../../models/consulta.model';
import { ConsultaService } from '../../services/consulta.service';
import { Paciente } from '../../models/paciente.model';
import { PacienteService } from '../../services/paciente.service';
import { Usuario } from '../../models/usuario.model';
import { UsuarioService } from '../../services/usuario.service';
import { AuthService } from '../../services/auth.service';
import { DocumentoService } from '../../services/documento.service';

interface DiaAgenda {
    data: string;
    rotulo: string;
    diaMes: string;
    diaSemana: string;
}

interface FormAgenda {
    pacienteBusca: string;
    pacienteId: number;
    dentistaId: number;
    data: string;
    horario: string;
    observacao: string;
}

@Component({
    selector: 'app-agenda',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink],
    templateUrl: './agenda.html',
    styleUrl: './agenda.scss'
})
export class Agenda implements OnInit {

    consultas: Consulta[] = [];
    consultaArrastando?: Consulta;
    consultaDetalhe?: Consulta;
    pacientesEncontrados: Paciente[] = [];
    dentistas: Usuario[] = [];

    carregando = false;
    salvando = false;
    gerandoPdf = false;
    buscandoPacientes = false;
    modalAberto = false;

    erro = '';
    sucesso = '';

    dataReferencia = this.dataHoje();
    diasSemana: DiaAgenda[] = [];
    horarios: string[] = [];

    pacienteSelecionado?: Paciente;
    dentistaLogadoNome = '';

    form: FormAgenda = this.formularioInicial();

    constructor(
        private consultaService: ConsultaService,
        private pacienteService: PacienteService,
        private usuarioService: UsuarioService,
        private authService: AuthService,
        private documentoService: DocumentoService
    ) { }

    ngOnInit(): void {
        this.horarios = this.gerarHorarios();
        this.montarSemana();
        this.prepararDentistaLogado();
        this.carregarDentistas();
        this.carregarConsultasSemana();
    }

    carregarConsultasSemana(): void {
        this.erro = '';
        this.sucesso = '';
        this.carregando = true;

        const inicio = `${this.diasSemana[0].data}T00:00:00`;
        const fim = `${this.diasSemana[this.diasSemana.length - 1].data}T23:59:59`;

        this.consultaService.listar(inicio, fim).subscribe({
            next: (dados) => {
                this.consultas = dados.filter(consulta => consulta.status !== 'CANCELADA');
                this.carregando = false;
            },
            error: () => {
                this.erro = 'Erro ao carregar agenda';
                this.carregando = false;
            }
        });
    }

    carregarDentistas(): void {
        if (!this.isAdmin()) {
            return;
        }

        this.usuarioService.listar().subscribe({
            next: (usuarios) => {
                this.dentistas = usuarios.filter(usuario =>
                    (usuario as any).perfil === 'DENTISTA' && (usuario as any).ativo !== false
                );
            },
            error: () => {
                this.erro = 'Erro ao carregar dentistas';
            }
        });
    }

    prepararDentistaLogado(): void {
        const usuarioLogado = this.authService.obterUsuario() as any;

        if (!usuarioLogado) {
            return;
        }

        this.dentistaLogadoNome = usuarioLogado.nome || 'Dentista';

        if (!this.isAdmin()) {
            this.form.dentistaId = Number(usuarioLogado.id || 0);
        }
    }

    abrirNovaConsulta(): void {
        this.erro = '';
        this.sucesso = '';
        this.modalAberto = true;
        this.pacienteSelecionado = undefined;
        this.pacientesEncontrados = [];

        this.form = this.formularioInicial();
        this.prepararDentistaLogado();

        const primeiroDia = this.diasSemana[0]?.data || this.dataHoje();
        this.form.data = primeiroDia;
        this.form.horario = this.primeiroHorarioDisponivel(primeiroDia);
    }

    abrirConsultaNoSlot(data: string, horario: string): void {
        if (this.slotOcupado(data, horario)) {
            return;
        }

        this.abrirNovaConsulta();
        this.form.data = data;
        this.form.horario = horario;
    }

    fecharModal(): void {
        this.modalAberto = false;
        this.salvando = false;
        this.erro = '';
        this.pacienteSelecionado = undefined;
        this.pacientesEncontrados = [];
    }

    buscarPacientes(): void {
        this.erro = '';
        this.pacienteSelecionado = undefined;
        this.form.pacienteId = 0;

        const termo = this.form.pacienteBusca.trim();

        if (termo.length < 2) {
            this.pacientesEncontrados = [];
            return;
        }

        this.buscandoPacientes = true;

        this.pacienteService.listar(termo).subscribe({
            next: (pacientes) => {
                this.pacientesEncontrados = pacientes;
                this.buscandoPacientes = false;
            },
            error: () => {
                this.erro = 'Erro ao pesquisar paciente';
                this.buscandoPacientes = false;
            }
        });
    }

    selecionarPaciente(paciente: Paciente): void {
        this.pacienteSelecionado = paciente;
        this.form.pacienteId = paciente.id;
        this.form.pacienteBusca = paciente.nome;
        this.pacientesEncontrados = [];

        if (paciente.dentistaId) {
            this.form.dentistaId = Number(paciente.dentistaId);
        }
    }

    salvar(): void {
        this.erro = '';
        this.sucesso = '';

        if (!this.form.pacienteId) {
            this.erro = 'Selecione um paciente existente para agendar a consulta';
            return;
        }

        if (!this.form.dentistaId) {
            this.erro = 'Selecione o dentista responsável pela consulta';
            return;
        }

        if (
            this.pacienteSelecionado?.dentistaId &&
            Number(this.form.dentistaId) !== Number(this.pacienteSelecionado.dentistaId)
        ) {
            this.erro = `Este paciente está vinculado ao dentista ${this.pacienteSelecionado.dentistaNome || 'responsável cadastrado'}. Selecione o dentista correto.`;
            this.form.dentistaId = Number(this.pacienteSelecionado.dentistaId);
            return;
        }

        if (!this.form.data) {
            this.erro = 'Selecione a data da consulta';
            return;
        }

        if (!this.form.horario) {
            this.erro = 'Selecione um horário disponível';
            return;
        }

        if (this.slotOcupado(this.form.data, this.form.horario)) {
            this.erro = 'Este horário já está ocupado. Selecione outro horário.';
            return;
        }

        const inicio = `${this.form.data}T${this.form.horario}:00`;
        const fim = this.somarMinutos(inicio, 30);

        const request: ConsultaRequest = {
            pacienteId: Number(this.form.pacienteId),
            dentistaId: Number(this.form.dentistaId),
            dataHoraInicio: inicio,
            dataHoraFim: fim,
            observacao: this.form.observacao || ''
        };

        this.salvando = true;

        this.consultaService.criar(request).subscribe({
            next: () => {
                this.salvando = false;
                this.fecharModal();
                this.sucesso = 'Consulta agendada com sucesso';
                this.carregarConsultasSemana();
            },
            error: (erro) => {
                this.salvando = false;
                this.erro =
                    erro?.error?.erro ||
                    erro?.error?.message ||
                    'Erro ao salvar consulta';
            }
        });
    }


    iniciarArraste(consulta: Consulta, event: DragEvent): void {
        this.consultaArrastando = consulta;
        this.erro = '';
        this.sucesso = '';

        if (event.dataTransfer) {
            event.dataTransfer.effectAllowed = 'move';
            event.dataTransfer.setData('text/plain', String(consulta.id));
        }
    }

    permitirSoltar(event: DragEvent): void {
        event.preventDefault();

        if (event.dataTransfer) {
            event.dataTransfer.dropEffect = 'move';
        }
    }

    soltarConsulta(data: string, horario: string, event: DragEvent): void {
        event.preventDefault();

        if (!this.consultaArrastando) {
            return;
        }

        const consultaDestino = this.consultaNoSlot(data, horario);

        if (consultaDestino) {
            this.erro = 'Este horário já possui uma consulta agendada';
            this.consultaArrastando = undefined;
            return;
        }

        const inicio = `${data}T${horario}:00`;
        const fim = this.somarMinutos(inicio, 30);

        const request: ConsultaRequest = {
            pacienteId: this.consultaArrastando.pacienteId,
            dentistaId: this.consultaArrastando.dentistaId,
            dataHoraInicio: inicio,
            dataHoraFim: fim,
            observacao: this.consultaArrastando.observacao || ''
        };

        this.salvando = true;
        this.erro = '';
        this.sucesso = '';

        this.consultaService.atualizar(this.consultaArrastando.id, request).subscribe({
            next: () => {
                this.salvando = false;
                this.sucesso = 'Consulta reagendada com sucesso';
                this.consultaArrastando = undefined;
                this.carregarConsultasSemana();
            },
            error: (erro) => {
                this.salvando = false;
                this.consultaArrastando = undefined;
                this.erro =
                    erro?.error?.erro ||
                    erro?.error?.message ||
                    'Erro ao reagendar consulta';
                this.carregarConsultasSemana();
            }
        });
    }

    finalizarArraste(): void {
        this.consultaArrastando = undefined;
    }

    corDentista(dentistaId: number): string {
        const dentistasUnicos = Array.from(
            new Set(this.consultas.map(consulta => consulta.dentistaId))
        ).sort((a, b) => a - b);

        const indice = dentistasUnicos.indexOf(dentistaId);

        return indice % 2 === 0 ? 'dentista-cor-1' : 'dentista-cor-2';
    }

    abrirDetalheConsulta(consulta: Consulta): void {
        if (this.consultaArrastando) {
            return;
        }

        this.consultaDetalhe = consulta;
    }

    fecharDetalheConsulta(): void {
        this.consultaDetalhe = undefined;
    }

    formatarDataConsulta(dataHora: string): string {
        if (!dataHora) {
            return '-';
        }

        return new Date(dataHora).toLocaleDateString('pt-BR');
    }

    excluir(consulta: Consulta): void {
        const mensagem = `Confirma excluir a consulta de ${consulta.pacienteNome} às ${this.formatarHora(consulta.dataHoraInicio)}?`;

        if (!confirm(mensagem)) {
            return;
        }

        this.consultaService.excluir(consulta.id).subscribe({
            next: () => {
                this.sucesso = 'Consulta excluída com sucesso';
                this.carregarConsultasSemana();
            },
            error: () => {
                this.erro = 'Erro ao excluir consulta';
            }
        });
    }


    gerarPdfSemana(): void {
        if (!this.diasSemana.length) {
            this.erro = 'Semana não identificada para geração do PDF';
            return;
        }

        const inicio = `${this.diasSemana[0].data}T00:00:00`;
        const fim = `${this.diasSemana[this.diasSemana.length - 1].data}T23:59:59`;

        this.gerarPdfAgenda(inicio, fim, 'semanal');
    }

    gerarPdfMes(): void {
        const dataBase = this.criarDataLocal(this.dataReferencia);
        const primeiroDia = new Date(dataBase.getFullYear(), dataBase.getMonth(), 1);
        const ultimoDia = new Date(dataBase.getFullYear(), dataBase.getMonth() + 1, 0);

        const inicio = `${this.formatarDataInput(primeiroDia)}T00:00:00`;
        const fim = `${this.formatarDataInput(ultimoDia)}T23:59:59`;

        this.gerarPdfAgenda(inicio, fim, 'mensal');
    }

    private gerarPdfAgenda(inicio: string, fim: string, tipo: 'semanal' | 'mensal'): void {
        this.erro = '';
        this.sucesso = '';
        this.gerandoPdf = true;

        this.documentoService.gerarAgenda(inicio, fim, tipo).subscribe({
            next: (pdf: Blob) => {
                this.gerandoPdf = false;
                this.salvarArquivoPdf(pdf, this.nomeArquivoAgenda(tipo));
                this.sucesso = tipo === 'mensal'
                    ? 'PDF mensal da agenda gerado com sucesso'
                    : 'PDF semanal da agenda gerado com sucesso';
            },
            error: (erro: any) => {
                this.gerandoPdf = false;
                this.erro =
                    erro?.error?.erro ||
                    erro?.error?.message ||
                    'Erro ao gerar PDF da agenda';
            }
        });
    }

    private salvarArquivoPdf(pdf: Blob, nomeArquivo: string): void {
        const blob = new Blob([pdf], { type: 'application/pdf' });
        const url = window.URL.createObjectURL(blob);

        const link = document.createElement('a');
        link.href = url;
        link.download = nomeArquivo;

        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);

        setTimeout(() => {
            window.URL.revokeObjectURL(url);
        }, 3000);
    }

    private nomeArquivoAgenda(tipo: 'semanal' | 'mensal'): string {

        const referencia = this.criarDataLocal(this.dataReferencia);

        const mes = String(referencia.getMonth() + 1).padStart(2, '0');
        const ano = referencia.getFullYear();

        if (tipo === 'mensal') {
            return `agenda_mensal_${mes}_${ano}.pdf`;
        }

        const primeiroDiaSemana = this.diasSemana[0]?.diaMes.replace('/', '-') ?? '';
        const ultimoDiaSemana = this.diasSemana[this.diasSemana.length - 1]?.diaMes.replace('/', '-') ?? '';

        return `agenda_semanal_${primeiroDiaSemana}_a_${ultimoDiaSemana}_${ano}.pdf`;
    }

    semanaAnterior(): void {
        this.dataReferencia = this.formatarDataInput(this.adicionarDias(this.dataReferencia, -7));
        this.montarSemana();
        this.carregarConsultasSemana();
    }

    proximaSemana(): void {
        this.dataReferencia = this.formatarDataInput(this.adicionarDias(this.dataReferencia, 7));
        this.montarSemana();
        this.carregarConsultasSemana();
    }

    irParaHoje(): void {
        this.dataReferencia = this.dataHoje();
        this.montarSemana();
        this.carregarConsultasSemana();
    }

    alterarSemanaPelaData(): void {
        this.montarSemana();
        this.carregarConsultasSemana();
    }

    consultaNoSlot(data: string, horario: string): Consulta | undefined {
        return this.consultas.find(consulta =>
            this.dataDaConsulta(consulta.dataHoraInicio) === data &&
            this.horaDaConsulta(consulta.dataHoraInicio) === horario
        );
    }

    slotOcupado(data: string, horario: string): boolean {
        return !!this.consultaNoSlot(data, horario);
    }

    horariosDisponiveisParaFormulario(): string[] {
        if (!this.form.data) {
            return [];
        }

        return this.horarios.filter(horario => !this.slotOcupado(this.form.data, horario));
    }

    pacienteNaoEncontrado(): boolean {
        return this.form.pacienteBusca.trim().length >= 2 &&
            !this.buscandoPacientes &&
            !this.pacienteSelecionado &&
            this.pacientesEncontrados.length === 0;
    }

    isAdmin(): boolean {
        return this.authService.isAdmin();
    }

    formatarHora(data: string): string {
        return this.horaDaConsulta(data);
    }

    formatarPeriodo(consulta: Consulta): string {
        return `${this.formatarHora(consulta.dataHoraInicio)} - ${this.formatarHora(consulta.dataHoraFim)}`;
    }

    periodoSemana(): string {
        if (!this.diasSemana.length) {
            return '';
        }

        const primeiro = this.diasSemana[0];
        const ultimo = this.diasSemana[this.diasSemana.length - 1];

        return `${primeiro.diaMes} a ${ultimo.diaMes}`;
    }

    private montarSemana(): void {
        const dataBase = this.criarDataLocal(this.dataReferencia);
        const diaSemana = dataBase.getDay();
        const distanciaSegunda = diaSemana === 0 ? -6 : 1 - diaSemana;
        const segunda = new Date(dataBase);
        segunda.setDate(dataBase.getDate() + distanciaSegunda);

        this.diasSemana = Array.from({ length: 5 }, (_, index) => {
            const data = new Date(segunda);
            data.setDate(segunda.getDate() + index);

            return {
                data: this.formatarDataInput(data),
                rotulo: data.toLocaleDateString('pt-BR', { weekday: 'short' }).replace('.', ''),
                diaMes: data.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' }),
                diaSemana: data.toLocaleDateString('pt-BR', { weekday: 'long' })
            };
        });
    }

    private gerarHorarios(): string[] {
        const horarios: string[] = [];

        for (let hora = 8; hora <= 21; hora++) {
            horarios.push(`${hora.toString().padStart(2, '0')}:00`);

            if (hora < 21) {
                horarios.push(`${hora.toString().padStart(2, '0')}:30`);
            }
        }

        return horarios;
    }

    private primeiroHorarioDisponivel(data: string): string {
        return this.horarios.find(horario => !this.slotOcupado(data, horario)) || '';
    }

    private formularioInicial(): FormAgenda {
        return {
            pacienteBusca: '',
            pacienteId: 0,
            dentistaId: 0,
            data: '',
            horario: '',
            observacao: ''
        };
    }

    private somarMinutos(dataHora: string, minutos: number): string {
        const data = new Date(dataHora);
        data.setMinutes(data.getMinutes() + minutos);

        const dataParte = this.formatarDataInput(data);
        const horaParte = `${String(data.getHours()).padStart(2, '0')}:${String(data.getMinutes()).padStart(2, '0')}`;

        return `${dataParte}T${horaParte}:00`;
    }

    private dataDaConsulta(dataHora: string): string {
        return dataHora.substring(0, 10);
    }

    private horaDaConsulta(dataHora: string): string {
        return dataHora.substring(11, 16);
    }

    private dataHoje(): string {
        return this.formatarDataInput(new Date());
    }

    private adicionarDias(data: string, dias: number): Date {
        const novaData = this.criarDataLocal(data);
        novaData.setDate(novaData.getDate() + dias);
        return novaData;
    }

    private criarDataLocal(data: string): Date {
        const [ano, mes, dia] = data.split('-').map(Number);
        return new Date(ano, mes - 1, dia);
    }

    private formatarDataInput(data: Date): string {
        const ano = data.getFullYear();
        const mes = String(data.getMonth() + 1).padStart(2, '0');
        const dia = String(data.getDate()).padStart(2, '0');

        return `${ano}-${mes}-${dia}`;
    }

    nomeResumido(nome: string): string {
        if (!nome) {
            return '-';
        }

        const partes = nome.trim().split(/\s+/);

        if (partes.length === 1) {
            return partes[0];
        }

        return `${partes[0]} ${partes[partes.length - 1]}`;
    }
}
