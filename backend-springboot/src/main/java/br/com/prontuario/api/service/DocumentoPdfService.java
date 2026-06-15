package br.com.prontuario.api.service;

import br.com.prontuario.api.dto.AtestadoRequest;
import br.com.prontuario.api.dto.ProntuarioPdfRequest;
import br.com.prontuario.api.dto.ReceituarioRequest;
import br.com.prontuario.api.entity.Anamnese;
import br.com.prontuario.api.entity.Atendimento;
import br.com.prontuario.api.entity.Odontograma;
import br.com.prontuario.api.entity.Orcamento;
import br.com.prontuario.api.entity.OrcamentoItem;
import br.com.prontuario.api.entity.OrcamentoPagamento;
import br.com.prontuario.api.entity.Paciente;
import br.com.prontuario.api.entity.Usuario;
import br.com.prontuario.api.repository.AnamneseRepository;
import br.com.prontuario.api.repository.AtendimentoRepository;
import br.com.prontuario.api.repository.OdontogramaRepository;
import br.com.prontuario.api.repository.OrcamentoPagamentoRepository;
import br.com.prontuario.api.repository.OrcamentoRepository;
import br.com.prontuario.api.repository.PacienteRepository;
import br.com.prontuario.api.repository.UsuarioRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class DocumentoPdfService {

    private static final Color AZUL_ESCURO = new Color(8, 43, 94);
    private static final Color AZUL_MEDIO = new Color(0, 102, 145);
    private static final Color AZUL_CLARO = new Color(20, 150, 160);
    private static final Color CINZA_TEXTO = new Color(80, 80, 80);
    private static final Color MARCA_DAGUA = new Color(232, 244, 248);
    private static final Color CINZA_CLARO = new Color(245, 247, 250);

    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final AnamneseRepository anamneseRepository;
    private final OdontogramaRepository odontogramaRepository;
    private final AtendimentoRepository atendimentoRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final OrcamentoPagamentoRepository orcamentoPagamentoRepository;
    private final PacienteService pacienteService;

    public DocumentoPdfService(
            PacienteRepository pacienteRepository,
            UsuarioRepository usuarioRepository,
            AnamneseRepository anamneseRepository,
            OdontogramaRepository odontogramaRepository,
            AtendimentoRepository atendimentoRepository,
            OrcamentoRepository orcamentoRepository,
            OrcamentoPagamentoRepository orcamentoPagamentoRepository,
            PacienteService pacienteService) {
        this.pacienteRepository = pacienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.anamneseRepository = anamneseRepository;
        this.odontogramaRepository = odontogramaRepository;
        this.atendimentoRepository = atendimentoRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.orcamentoPagamentoRepository = orcamentoPagamentoRepository;
        this.pacienteService = pacienteService;
    }

    public byte[] gerarAtestado(Long pacienteId, AtestadoRequest request, String emailUsuarioLogado) {
        Paciente paciente = pacienteService.buscarPorId(pacienteId, emailUsuarioLogado);
        Usuario dentista = buscarDentista(emailUsuarioLogado);

        validarAtestadoRequest(request);
        validarDadosDentista(dentista);

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 0, 0, 0, 0);
            PdfWriter writer = PdfWriter.getInstance(document, output);

            document.open();

            PdfContentByte canvas = writer.getDirectContent();

            desenharLayout(canvas);
            escreverConteudoAtestado(canvas, paciente, request, dentista);

            document.close();

            return output.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar atestado em PDF", e);
        }
    }

    public byte[] gerarReceituario(Long pacienteId, ReceituarioRequest request, String emailUsuarioLogado) {
        Paciente paciente = pacienteService.buscarPorId(pacienteId, emailUsuarioLogado);
        Usuario dentista = buscarDentista(emailUsuarioLogado);

        validarReceituarioRequest(request);
        validarDadosDentista(dentista);

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 0, 0, 0, 0);
            PdfWriter writer = PdfWriter.getInstance(document, output);

            document.open();

            PdfContentByte canvas = writer.getDirectContent();

            desenharLayout(canvas);
            escreverConteudoReceituario(canvas, paciente, request, dentista);

            document.close();

            return output.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar receituário em PDF", e);
        }
    }

    public byte[] gerarProntuario(Long pacienteId, ProntuarioPdfRequest request, String emailUsuarioLogado) {
        Paciente paciente = pacienteService.buscarPorId(pacienteId, emailUsuarioLogado);
        Usuario dentista = buscarDentista(emailUsuarioLogado);

        validarProntuarioRequest(request);
        validarDadosDentista(dentista);

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            Document document = new Document(PageSize.A4, 0, 0, 0, 0);
            PdfWriter writer = PdfWriter.getInstance(document, output);

            document.open();

            ManualProntuarioContext contexto = new ManualProntuarioContext(
                    document,
                    writer,
                    tituloProntuario(request));

            contexto.iniciarPrimeiraPagina();

            adicionarDadosPacienteManual(contexto, paciente);

            if (Boolean.TRUE.equals(request.getIncluirAnamnese())) {
                adicionarSecaoAnamneseManual(contexto, paciente.getId());
            }

            if (Boolean.TRUE.equals(request.getIncluirOdontograma())) {
                adicionarSecaoOdontogramaManual(contexto, paciente.getId());
            }

            if (Boolean.TRUE.equals(request.getIncluirAtendimentos())) {
                adicionarSecaoAtendimentosManual(contexto, paciente.getId());
            }

            contexto.desenharAssinatura(dentista);

            document.close();

            return output.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar prontuário em PDF", e);
        }
    }

    public byte[] gerarOrcamento(Long orcamentoId, String emailUsuarioLogado) {
        Usuario dentista = buscarDentista(emailUsuarioLogado);
        validarDadosDentista(dentista);

        Orcamento orcamento = orcamentoRepository.findById(orcamentoId)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            Document document = new Document(PageSize.A4, 70, 70, 230, 120);
            PdfWriter writer = PdfWriter.getInstance(document, output);
            writer.setPageEvent(new DocumentoClinicoPageEvent("ORÇAMENTO ODONTOLÓGICO"));

            document.open();

            Font secao = new Font(Font.HELVETICA, 12, Font.BOLD, AZUL_ESCURO);
            Font normal = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
            Font normalNegrito = new Font(Font.HELVETICA, 10, Font.BOLD, Color.BLACK);

            adicionarDadosOrcamentoPdf(document, orcamento, secao, normal, normalNegrito);
            adicionarItensOrcamentoPdf(document, orcamento, normal, normalNegrito);
            adicionarResumoOrcamentoPdf(document, orcamento, normal, normalNegrito);
            adicionarObservacoesOrcamentoPdf(document, orcamento, secao, normal);

            List<OrcamentoPagamento> pagamentos = orcamentoPagamentoRepository
                    .findByOrcamentoIdOrderByDataPagamentoDesc(orcamento.getId());

            if (!pagamentos.isEmpty()) {
                adicionarPagamentosOrcamentoPdf(document, orcamento, normal, normalNegrito);
            }

            adicionarDataAssinaturaNoFinal(document, writer, dentista, normal, normalNegrito, normal);

            document.close();

            return output.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar orçamento em PDF", e);
        }
    }

    private Usuario buscarDentista(String emailUsuarioLogado) {
        return usuarioRepository.findByEmail(emailUsuarioLogado)
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado"));
    }

    private void desenharLayout(PdfContentByte canvas) {
        desenharMarcaDaguaDente(canvas);
        desenharBarraLateralDireita(canvas);
        desenharDetalheInferiorEsquerdo(canvas);
    }

    private void desenharBarraLateralDireita(PdfContentByte canvas) {
        canvas.setColorFill(AZUL_ESCURO);
        canvas.rectangle(570, 80, 10, 700);
        canvas.fill();

        canvas.setColorFill(AZUL_CLARO);
        canvas.rectangle(570, 35, 10, 40);
        canvas.fill();
    }

    private void desenharDetalheInferiorEsquerdo(PdfContentByte canvas) {
        canvas.setColorFill(AZUL_MEDIO);
        canvas.roundRectangle(0, 55, 52, 12, 6);
        canvas.fill();

        canvas.setColorFill(new Color(150, 210, 205));
        canvas.roundRectangle(50, 55, 22, 12, 6);
        canvas.fill();

        canvas.setColorFill(AZUL_CLARO);
        canvas.roundRectangle(0, 28, 52, 12, 6);
        canvas.fill();

        canvas.setColorFill(new Color(150, 210, 205));
        canvas.roundRectangle(50, 28, 22, 12, 6);
        canvas.fill();
    }

    private void desenharMarcaDaguaDente(PdfContentByte canvas) {
        desenharDenteEstilizado(canvas, 185, 535, 2.45f, MARCA_DAGUA, MARCA_DAGUA, 13f, 6f);
    }

    private void escreverConteudoAtestado(
            PdfContentByte canvas,
            Paciente paciente,
            AtestadoRequest request,
            Usuario dentista) {

        Font tituloCabecalho = new Font(Font.HELVETICA, 21, Font.BOLD, AZUL_ESCURO);
        Font subtitulo = new Font(Font.HELVETICA, 10, Font.NORMAL, CINZA_TEXTO);
        Font titulo = new Font(Font.HELVETICA, 21, Font.BOLD, AZUL_ESCURO);
        Font secao = new Font(Font.HELVETICA, 13, Font.BOLD, AZUL_ESCURO);
        Font normal = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.BLACK);
        Font dataNegrito = new Font(Font.HELVETICA, 11, Font.BOLD, AZUL_ESCURO);
        Font assinaturaNome = new Font(Font.HELVETICA, 14, Font.BOLD, AZUL_ESCURO);
        Font assinaturaDados = new Font(Font.HELVETICA, 13, Font.NORMAL, Color.BLACK);
        Font rodape = new Font(Font.HELVETICA, 9, Font.NORMAL, AZUL_MEDIO);

        escreverCabecalho(canvas, tituloCabecalho, subtitulo);
        escreverTitulo(canvas, "ATESTADO", titulo);

        escreverDadosPaciente(canvas, paciente, secao, normal, 555);

        escreverTextoLongo(canvas, request.getTexto(), normal, 90, 430, 505, 330);

        escreverData(canvas, normal, dataNegrito, 275);
        escreverAssinatura(canvas, dentista, assinaturaNome, assinaturaDados);
        escreverRodape(canvas, rodape);
    }

    private void escreverConteudoReceituario(
            PdfContentByte canvas,
            Paciente paciente,
            ReceituarioRequest request,
            Usuario dentista) {

        Font tituloCabecalho = new Font(Font.HELVETICA, 21, Font.BOLD, AZUL_ESCURO);
        Font subtitulo = new Font(Font.HELVETICA, 10, Font.NORMAL, CINZA_TEXTO);
        Font titulo = new Font(Font.HELVETICA, 21, Font.BOLD, AZUL_ESCURO);
        Font secao = new Font(Font.HELVETICA, 13, Font.BOLD, AZUL_ESCURO);
        Font normal = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.BLACK);
        Font dataNegrito = new Font(Font.HELVETICA, 11, Font.BOLD, AZUL_ESCURO);
        Font assinaturaNome = new Font(Font.HELVETICA, 14, Font.BOLD, AZUL_ESCURO);
        Font assinaturaDados = new Font(Font.HELVETICA, 13, Font.NORMAL, Color.BLACK);
        Font rodape = new Font(Font.HELVETICA, 9, Font.NORMAL, AZUL_MEDIO);

        escreverCabecalho(canvas, tituloCabecalho, subtitulo);
        escreverTitulo(canvas, "RECEITUÁRIO", titulo);

        escreverDadosPaciente(canvas, paciente, secao, normal, 555);

        escrever(canvas, "PRESCRIÇÃO", secao, 90, 455, Element.ALIGN_LEFT);
        escreverTextoLongo(canvas, request.getPrescricao(), normal, 90, 430, 505, 305);

        escreverData(canvas, normal, dataNegrito, 230);
        escreverAssinatura(canvas, dentista, assinaturaNome, assinaturaDados);
        escreverRodape(canvas, rodape);
    }

    private void escreverConteudoProntuario(
            PdfContentByte canvas,
            Paciente paciente,
            ProntuarioPdfRequest request,
            Usuario dentista) {

        Font tituloCabecalho = new Font(Font.HELVETICA, 21, Font.BOLD, AZUL_ESCURO);
        Font subtitulo = new Font(Font.HELVETICA, 10, Font.NORMAL, CINZA_TEXTO);
        Font titulo = new Font(Font.HELVETICA, 24, Font.BOLD, AZUL_ESCURO);
        Font secao = new Font(Font.HELVETICA, 13, Font.BOLD, AZUL_ESCURO);
        Font normal = new Font(Font.HELVETICA, 11, Font.NORMAL, Color.BLACK);
        Font normalNegrito = new Font(Font.HELVETICA, 11, Font.BOLD, Color.BLACK);
        Font dataNegrito = new Font(Font.HELVETICA, 11, Font.BOLD, AZUL_ESCURO);
        Font assinaturaNome = new Font(Font.HELVETICA, 14, Font.BOLD, AZUL_ESCURO);
        Font assinaturaDados = new Font(Font.HELVETICA, 13, Font.NORMAL, Color.BLACK);
        Font rodape = new Font(Font.HELVETICA, 9, Font.NORMAL, AZUL_MEDIO);

        escreverCabecalho(canvas, tituloCabecalho, subtitulo);
        escreverTitulo(canvas, tituloProntuario(request), titulo);

        escreverDadosPaciente(canvas, paciente, secao, normal, 555);

        float y = 455;

        if (Boolean.TRUE.equals(request.getIncluirAnamnese())) {
            y = escreverSecaoAnamnese(canvas, paciente.getId(), secao, normal, normalNegrito, y);
        }

        if (Boolean.TRUE.equals(request.getIncluirOdontograma())) {
            y = escreverSecaoOdontograma(canvas, paciente.getId(), secao, normal, normalNegrito, y);
        }

        if (Boolean.TRUE.equals(request.getIncluirAtendimentos())) {
            y = escreverSecaoAtendimentos(canvas, paciente.getId(), secao, normal, normalNegrito, y);
        }

        escreverData(canvas, normal, dataNegrito, 230);
        escreverAssinatura(canvas, dentista, assinaturaNome, assinaturaDados);
        escreverRodape(canvas, rodape);
    }

    private String tituloProntuario(ProntuarioPdfRequest request) {
        int total = 0;

        if (Boolean.TRUE.equals(request.getIncluirAnamnese())) {
            total++;
        }

        if (Boolean.TRUE.equals(request.getIncluirOdontograma())) {
            total++;
        }

        if (Boolean.TRUE.equals(request.getIncluirAtendimentos())) {
            total++;
        }

        if (total > 1) {
            return "PRONTUÁRIO ODONTOLÓGICO";
        }

        if (Boolean.TRUE.equals(request.getIncluirAnamnese())) {
            return "ANAMNESE";
        }

        if (Boolean.TRUE.equals(request.getIncluirOdontograma())) {
            return "ODONTOGRAMA";
        }

        return "ATENDIMENTOS";
    }

    private float escreverSecaoAnamnese(
            PdfContentByte canvas,
            Long pacienteId,
            Font secao,
            Font normal,
            Font normalNegrito,
            float y) {

        y = garantirEspaco(y, 130);
        escrever(canvas, "ANAMNESE", secao, 90, y, Element.ALIGN_LEFT);
        y -= 28;

        Anamnese anamnese = anamneseRepository.findByPacienteId(pacienteId).orElse(null);

        if (anamnese == null) {
            escrever(canvas, "Nenhuma anamnese cadastrada.", normal, 90, y, Element.ALIGN_LEFT);
            return y - 35;
        }

        escreverLinhaRotuloValor(canvas, "Hipertensão:", simNao(anamnese.getHipertensao()), normalNegrito, normal, 90,
                y);
        escreverLinhaRotuloValor(canvas, "Diabetes:", simNao(anamnese.getDiabetes()), normalNegrito, normal, 285, y);
        y -= 22;

        escreverLinhaRotuloValor(canvas, "Fumante:", simNao(anamnese.getFumante()), normalNegrito, normal, 90, y);
        escreverLinhaRotuloValor(canvas, "Grávida:", simNao(anamnese.getGravida()), normalNegrito, normal, 285, y);
        y -= 28;

        escrever(canvas, "Alergias:", normalNegrito, 90, y, Element.ALIGN_LEFT);
        y -= 18;
        escreverTextoLongo(canvas, valor(anamnese.getAlergias()), normal, 90, y, 505, y - 45);
        y -= 60;

        escrever(canvas, "Medicamentos:", normalNegrito, 90, y, Element.ALIGN_LEFT);
        y -= 18;
        escreverTextoLongo(canvas, valor(anamnese.getMedicamentos()), normal, 90, y, 505, y - 45);
        y -= 60;

        escrever(canvas, "Observações:", normalNegrito, 90, y, Element.ALIGN_LEFT);
        y -= 18;
        escreverTextoLongo(canvas, valor(anamnese.getObservacoes()), normal, 90, y, 505, y - 55);

        return y - 75;
    }

    private float escreverSecaoOdontograma(
            PdfContentByte canvas,
            Long pacienteId,
            Font secao,
            Font normal,
            Font normalNegrito,
            float y) {

        y = garantirEspaco(y, 210);
        escrever(canvas, "ODONTOGRAMA", secao, 90, y, Element.ALIGN_LEFT);
        y -= 28;

        List<Odontograma> registros = odontogramaRepository.findByPacienteIdOrderByNumeroDenteAsc(pacienteId);

        desenharOdontogramaSimplificado(canvas, registros, 90, y);
        y -= 130;

        if (registros.isEmpty()) {
            escrever(canvas, "Nenhum registro de odontograma cadastrado.", normal, 90, y, Element.ALIGN_LEFT);
            return y - 35;
        }

        escrever(canvas, "REGISTROS DO ODONTOGRAMA", normalNegrito, 90, y, Element.ALIGN_LEFT);
        y -= 24;

        for (Odontograma registro : registros) {
            y = garantirEspaco(y, 55);

            escreverLinhaRotuloValor(
                    canvas,
                    "Dente:",
                    String.valueOf(registro.getNumeroDente()),
                    normalNegrito,
                    normal,
                    90,
                    y);

            escreverLinhaRotuloValor(
                    canvas,
                    "Status:",
                    valor(registro.getStatus()),
                    normalNegrito,
                    normal,
                    180,
                    y);

            y -= 20;

            escrever(canvas, "Observação:", normalNegrito, 90, y, Element.ALIGN_LEFT);
            escreverTextoLongo(canvas, valor(registro.getObservacao()), normal, 165, y + 2, 505, y - 32);
            y -= 48;
        }

        return y - 10;
    }

    private float escreverSecaoAtendimentos(
            PdfContentByte canvas,
            Long pacienteId,
            Font secao,
            Font normal,
            Font normalNegrito,
            float y) {

        y = garantirEspaco(y, 120);
        escrever(canvas, "ATENDIMENTOS", secao, 90, y, Element.ALIGN_LEFT);
        y -= 28;

        List<Atendimento> atendimentos = atendimentoRepository.findByPacienteIdOrderByDataAtendimentoDesc(pacienteId);

        if (atendimentos.isEmpty()) {
            escrever(canvas, "Nenhum atendimento cadastrado.", normal, 90, y, Element.ALIGN_LEFT);
            return y - 35;
        }

        for (Atendimento atendimento : atendimentos) {
            y = garantirEspaco(y, 125);

            escreverLinhaRotuloValor(
                    canvas,
                    "Data:",
                    atendimento.getDataAtendimento() == null
                            ? "-"
                            : atendimento.getDataAtendimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    normalNegrito,
                    normal,
                    90,
                    y);
            y -= 24;

            escrever(canvas, "Queixa principal:", normalNegrito, 90, y, Element.ALIGN_LEFT);
            escreverTextoLongo(canvas, valor(atendimento.getQueixaPrincipal()), normal, 190, y + 2, 505, y - 28);
            y -= 40;

            escrever(canvas, "Evolução clínica:", normalNegrito, 90, y, Element.ALIGN_LEFT);
            escreverTextoLongo(canvas, valor(atendimento.getEvolucaoClinica()), normal, 190, y + 2, 505, y - 28);
            y -= 40;

            escrever(canvas, "Procedimento:", normalNegrito, 90, y, Element.ALIGN_LEFT);
            escreverTextoLongo(canvas, valor(atendimento.getProcedimentoRealizado()), normal, 190, y + 2, 505, y - 28);
            y -= 40;

            escrever(canvas, "Observações:", normalNegrito, 90, y, Element.ALIGN_LEFT);
            escreverTextoLongo(canvas, valor(atendimento.getObservacoes()), normal, 190, y + 2, 505, y - 28);
            y -= 52;
        }

        return y;
    }

    private float garantirEspaco(float y, float alturaNecessaria) {
        return y;
    }

    private void escreverDadosPaciente(PdfContentByte canvas, Paciente paciente, Font secao, Font normal, float y) {
        escrever(canvas, "DADOS DO PACIENTE", secao, 90, y, Element.ALIGN_LEFT);
        escrever(canvas, "Nome: " + valor(paciente.getNome()), normal, 90, y - 30, Element.ALIGN_LEFT);
        escrever(canvas, "CPF: " + valor(paciente.getCpf()), normal, 90, y - 55, Element.ALIGN_LEFT);
    }

    private void desenharOdontogramaSimplificado(
            PdfContentByte canvas,
            List<Odontograma> registros,
            float x,
            float y) {

        int[] superioresEsquerdo = { 18, 17, 16, 15, 14, 13, 12, 11 };
        int[] superioresDireito = { 21, 22, 23, 24, 25, 26, 27, 28 };
        int[] inferioresEsquerdo = { 48, 47, 46, 45, 44, 43, 42, 41 };
        int[] inferioresDireito = { 31, 32, 33, 34, 35, 36, 37, 38 };

        escrever(canvas, "Superior esquerdo", new Font(Font.HELVETICA, 8, Font.BOLD, AZUL_MEDIO), x, y,
                Element.ALIGN_LEFT);
        escrever(canvas, "Superior direito", new Font(Font.HELVETICA, 8, Font.BOLD, AZUL_MEDIO), x + 230, y,
                Element.ALIGN_LEFT);

        desenharLinhaDentes(canvas, superioresEsquerdo, registros, x, y - 28);
        desenharLinhaDentes(canvas, superioresDireito, registros, x + 230, y - 28);

        canvas.setColorStroke(AZUL_CLARO);
        canvas.setLineWidth(1f);
        canvas.moveTo(x, y - 58);
        canvas.lineTo(x + 415, y - 58);
        canvas.stroke();

        escrever(canvas, "Inferior esquerdo", new Font(Font.HELVETICA, 8, Font.BOLD, AZUL_MEDIO), x, y - 78,
                Element.ALIGN_LEFT);
        escrever(canvas, "Inferior direito", new Font(Font.HELVETICA, 8, Font.BOLD, AZUL_MEDIO), x + 230, y - 78,
                Element.ALIGN_LEFT);

        desenharLinhaDentes(canvas, inferioresEsquerdo, registros, x, y - 106);
        desenharLinhaDentes(canvas, inferioresDireito, registros, x + 230, y - 106);
    }

    private void desenharLinhaDentes(
            PdfContentByte canvas,
            int[] dentes,
            List<Odontograma> registros,
            float x,
            float y) {

        float largura = 24;
        float altura = 24;
        float espaco = 3;

        Font fonteDente = new Font(Font.HELVETICA, 8, Font.BOLD, Color.BLACK);

        for (int i = 0; i < dentes.length; i++) {
            int numero = dentes[i];
            float atualX = x + (i * (largura + espaco));

            boolean possuiRegistro = possuiRegistroDente(registros, numero);

            canvas.setColorFill(possuiRegistro ? new Color(255, 235, 235) : Color.WHITE);
            canvas.setColorStroke(possuiRegistro ? Color.RED : new Color(190, 205, 220));
            canvas.roundRectangle(atualX, y, largura, altura, 6);
            canvas.fillStroke();

            escrever(canvas, String.valueOf(numero), fonteDente, atualX + (largura / 2), y + 8, Element.ALIGN_CENTER);
        }
    }

    private boolean possuiRegistroDente(List<Odontograma> registros, int numeroDente) {
        String numeroDenteTexto = String.valueOf(numeroDente);

        for (Odontograma registro : registros) {
            if (registro.getNumeroDente() != null
                    && numeroDenteTexto.equals(registro.getNumeroDente())) {
                return true;
            }
        }

        return false;
    }

    private void escreverLinhaRotuloValor(
            PdfContentByte canvas,
            String rotulo,
            String valor,
            Font fonteRotulo,
            Font fonteValor,
            float x,
            float y) {

        Phrase phrase = new Phrase();
        phrase.add(new Chunk(rotulo + " ", fonteRotulo));
        phrase.add(new Chunk(valor(valor), fonteValor));
        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, phrase, x, y, 0);
    }

    private void escreverCabecalho(PdfContentByte canvas, Font tituloCabecalho, Font subtitulo) {
        escrever(canvas, "CONSULTÓRIO ODONTOLÓGICO", tituloCabecalho, 120, 765, Element.ALIGN_LEFT);
        escrever(canvas, "Cuidando do seu sorriso com saúde e qualidade", subtitulo, 120, 745, Element.ALIGN_LEFT);
        desenharIconeDente(canvas, 42, 780);
    }

    private void escreverTitulo(PdfContentByte canvas, String texto, Font titulo) {
        escrever(canvas, texto, titulo, 297, 650, Element.ALIGN_CENTER);

        canvas.setColorStroke(AZUL_CLARO);
        canvas.setLineWidth(1f);
        canvas.moveTo(120, 632);
        canvas.lineTo(475, 632);
        canvas.stroke();
    }

    private void escreverData(PdfContentByte canvas, Font normal, Font dataNegrito, float y) {
        Phrase data = new Phrase();
        data.add(new Chunk("Data: ", normal));
        data.add(new Chunk(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), dataNegrito));
        ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, data, 505, y, 0);
    }

    private void escreverAssinatura(
            PdfContentByte canvas,
            Usuario dentista,
            Font assinaturaNome,
            Font assinaturaDados) {

        canvas.setColorStroke(Color.BLACK);
        canvas.setLineWidth(1f);
        canvas.moveTo(175, 175);
        canvas.lineTo(420, 175);
        canvas.stroke();

        escrever(canvas, dentista.getNome(), assinaturaNome, 297, 155, Element.ALIGN_CENTER);

        if (dentista.getEspecialidade() != null && !dentista.getEspecialidade().isBlank()) {
            escrever(canvas, dentista.getEspecialidade(), assinaturaDados, 297, 132, Element.ALIGN_CENTER);
        }

        if (dentista.getCro() != null && !dentista.getCro().isBlank()) {
            escrever(canvas, dentista.getCro(), assinaturaDados, 297, 110, Element.ALIGN_CENTER);
        }
    }

    private void escreverRodape(PdfContentByte canvas, Font rodape) {

        escrever(
                canvas,
                "21 99158-0796 | 21 96863-4089",
                rodape,
                105,
                58,
                Element.ALIGN_LEFT);

        desenharIconeWhatsapp(canvas);

        escrever(
                canvas,
                "(consultas com hora marcada)",
                rodape,
                265,
                58,
                Element.ALIGN_LEFT);

        escrever(
                canvas,
                "Rua Dr. Feliciano Sodré, nº 215, sala 204 - Centro, São Gonçalo/RJ",
                rodape,
                105,
                36,
                Element.ALIGN_LEFT);
    }

    private void desenharIconeWhatsapp(PdfContentByte canvas) {

        try {
            Image whatsapp = Image.getInstance(
                    getClass().getResource("/imagens/icone_whatsapp.png"));

            whatsapp.scaleAbsolute(18f, 18f);
            whatsapp.setAbsolutePosition(245f, 52f);

            canvas.addImage(whatsapp);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void desenharIconeDente(PdfContentByte canvas, float x, float y) {
        desenharDenteEstilizado(canvas, x, y, 0.52f, AZUL_ESCURO, AZUL_CLARO, 4f, 2.2f);
    }

    private void desenharDenteEstilizado(
            PdfContentByte canvas,
            float x,
            float y,
            float escala,
            Color corPrincipal,
            Color corDetalhe,
            float linhaPrincipal,
            float linhaDetalhe) {

        canvas.saveState();

        canvas.setColorStroke(corPrincipal);
        canvas.setLineWidth(linhaPrincipal);

        canvas.moveTo(x + 22 * escala, y - 8 * escala);
        canvas.curveTo(x + 5 * escala, y - 2 * escala, x - 5 * escala, y - 25 * escala, x + 6 * escala,
                y - 50 * escala);
        canvas.curveTo(x + 18 * escala, y - 80 * escala, x + 22 * escala, y - 115 * escala, x + 31 * escala,
                y - 148 * escala);

        canvas.moveTo(x + 22 * escala, y - 8 * escala);
        canvas.curveTo(x + 42 * escala, y - 25 * escala, x + 70 * escala, y - 25 * escala, x + 91 * escala,
                y - 8 * escala);
        canvas.curveTo(x + 112 * escala, y - 25 * escala, x + 105 * escala, y - 58 * escala, x + 96 * escala,
                y - 84 * escala);
        canvas.curveTo(x + 86 * escala, y - 115 * escala, x + 82 * escala, y - 135 * escala, x + 78 * escala,
                y - 148 * escala);

        canvas.moveTo(x + 31 * escala, y - 148 * escala);
        canvas.curveTo(x + 42 * escala, y - 122 * escala, x + 67 * escala, y - 122 * escala, x + 78 * escala,
                y - 148 * escala);

        canvas.stroke();

        canvas.setColorStroke(corDetalhe);
        canvas.setLineWidth(linhaDetalhe);

        canvas.moveTo(x + 28 * escala, y - 34 * escala);
        canvas.curveTo(x + 46 * escala, y - 48 * escala, x + 70 * escala, y - 48 * escala, x + 88 * escala,
                y - 34 * escala);
        canvas.stroke();

        canvas.moveTo(x + 14 * escala, y - 108 * escala);
        canvas.curveTo(x + 42 * escala, y - 92 * escala, x + 75 * escala, y - 82 * escala, x + 103 * escala,
                y - 58 * escala);
        canvas.stroke();

        canvas.restoreState();
    }

    private void escrever(PdfContentByte canvas, String texto, Font fonte, float x, float y, int alinhamento) {
        ColumnText.showTextAligned(canvas, alinhamento, new Phrase(texto, fonte), x, y, 0);
    }

    private void escreverTextoLongo(
            PdfContentByte canvas,
            String texto,
            Font fonte,
            float x1,
            float y1,
            float x2,
            float y2) {

        ColumnText ct = new ColumnText(canvas);
        ct.setSimpleColumn(new Phrase(valor(texto), fonte), x1, y2, x2, y1, 18, Element.ALIGN_JUSTIFIED);

        try {
            ct.go();
        } catch (DocumentException e) {
            throw new RuntimeException("Erro ao escrever texto no documento", e);
        }
    }

    private void escreverConteudoProntuarioPaginado(
            Document document,
            PdfWriter writer,
            Paciente paciente,
            ProntuarioPdfRequest request,
            Usuario dentista) throws DocumentException {

        Font secao = new Font(Font.HELVETICA, 12, Font.BOLD, AZUL_ESCURO);
        Font normal = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
        Font normalNegrito = new Font(Font.HELVETICA, 10, Font.BOLD, Color.BLACK);
        Font assinaturaNome = new Font(Font.HELVETICA, 12, Font.BOLD, AZUL_ESCURO);
        Font assinaturaDados = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);

        adicionarDadosPaciente(document, paciente, secao, normal);

        if (Boolean.TRUE.equals(request.getIncluirAnamnese())) {
            adicionarSecaoAnamnese(document, paciente.getId(), secao, normal, normalNegrito);
        }

        if (Boolean.TRUE.equals(request.getIncluirOdontograma())) {
            adicionarSecaoOdontograma(document, writer, paciente.getId(), secao, normal, normalNegrito);
        }

        if (Boolean.TRUE.equals(request.getIncluirAtendimentos())) {
            adicionarSecaoAtendimentos(document, writer, paciente.getId(), secao, normal, normalNegrito);
        }

        adicionarDataAssinaturaNoFinal(document, writer, dentista, normal, assinaturaNome, assinaturaDados);
    }

    private void adicionarDadosPaciente(
            Document document,
            Paciente paciente,
            Font secao,
            Font normal) throws DocumentException {

        Paragraph titulo = new Paragraph("DADOS DO PACIENTE", secao);
        titulo.setSpacingAfter(14f);
        document.add(titulo);

        document.add(new Paragraph("Nome: " + valor(paciente.getNome()), normal));
        Paragraph cpf = new Paragraph("CPF: " + valor(paciente.getCpf()), normal);
        cpf.setSpacingAfter(22f);
        document.add(cpf);
    }

    private void adicionarSecaoAnamnese(
            Document document,
            Long pacienteId,
            Font secao,
            Font normal,
            Font normalNegrito) throws DocumentException {

        adicionarTituloSecao(document, "ANAMNESE", secao);

        Anamnese anamnese = anamneseRepository.findByPacienteId(pacienteId).orElse(null);

        if (anamnese == null) {
            Paragraph vazio = new Paragraph("Nenhuma anamnese cadastrada.", normal);
            vazio.setSpacingAfter(18f);
            document.add(vazio);
            return;
        }

        PdfPTable tabela = new PdfPTable(2);
        tabela.setWidthPercentage(100);
        tabela.setWidths(new float[] { 1f, 1f });
        tabela.setSpacingAfter(14f);

        adicionarCelulaRotuloValor(tabela, "Hipertensão:", simNao(anamnese.getHipertensao()), normalNegrito, normal);
        adicionarCelulaRotuloValor(tabela, "Diabetes:", simNao(anamnese.getDiabetes()), normalNegrito, normal);
        adicionarCelulaRotuloValor(tabela, "Fumante:", simNao(anamnese.getFumante()), normalNegrito, normal);
        adicionarCelulaRotuloValor(tabela, "Grávida:", simNao(anamnese.getGravida()), normalNegrito, normal);

        document.add(tabela);

        adicionarBlocoTexto(document, "Alergias:", valor(anamnese.getAlergias()), normalNegrito, normal);
        adicionarBlocoTexto(document, "Medicamentos:", valor(anamnese.getMedicamentos()), normalNegrito, normal);
        adicionarBlocoTexto(document, "Observações:", valor(anamnese.getObservacoes()), normalNegrito, normal);
    }

    private void adicionarSecaoOdontograma(
            Document document,
            PdfWriter writer,
            Long pacienteId,
            Font secao,
            Font normal,
            Font normalNegrito) throws DocumentException {

        adicionarTituloSecao(document, "ODONTOGRAMA", secao);

        List<Odontograma> registros = odontogramaRepository.findByPacienteIdOrderByNumeroDenteAsc(pacienteId);

        PdfPTable tabelaOdontograma = criarTabelaOdontogramaSimplificado(registros);
        tabelaOdontograma.setKeepTogether(true);
        garantirEspaco(document, writer, 145f);
        document.add(tabelaOdontograma);

        if (registros.isEmpty()) {
            Paragraph vazio = new Paragraph("Nenhum registro de odontograma cadastrado.", normal);
            vazio.setSpacingBefore(12f);
            vazio.setSpacingAfter(18f);
            document.add(vazio);
            return;
        }

        PdfPTable blocoRegistros = new PdfPTable(1);
        blocoRegistros.setWidthPercentage(100);
        blocoRegistros.setKeepTogether(true);
        blocoRegistros.setSpacingBefore(14f);
        blocoRegistros.setSpacingAfter(18f);

        PdfPCell blocoCell = new PdfPCell();
        blocoCell.setBorder(Rectangle.NO_BORDER);
        blocoCell.setPadding(0f);

        Paragraph subtitulo = new Paragraph("REGISTROS DO ODONTOGRAMA", normalNegrito);
        subtitulo.setSpacingAfter(10f);
        blocoCell.addElement(subtitulo);

        PdfPTable tabela = new PdfPTable(3);
        tabela.setWidthPercentage(100);
        tabela.setKeepTogether(true);
        tabela.setSplitRows(false);
        tabela.setWidths(new float[] { 0.8f, 1.4f, 3.8f });

        adicionarCabecalhoTabela(tabela, "Dente", normalNegrito);
        adicionarCabecalhoTabela(tabela, "Status", normalNegrito);
        adicionarCabecalhoTabela(tabela, "Observação", normalNegrito);

        for (Odontograma registro : registros) {
            adicionarCelulaTabela(tabela, valor(registro.getNumeroDente()), normal);
            adicionarCelulaTabela(tabela, valor(registro.getStatus()), normal);
            adicionarCelulaTabela(tabela, valor(registro.getObservacao()), normal);
        }

        blocoCell.addElement(tabela);
        blocoRegistros.addCell(blocoCell);

        garantirEspaco(document, writer, 145f);
        document.add(blocoRegistros);
    }

    private void adicionarSecaoAtendimentos(
            Document document,
            PdfWriter writer,
            Long pacienteId,
            Font secao,
            Font normal,
            Font normalNegrito) throws DocumentException {

        adicionarTituloSecao(document, "ATENDIMENTOS", secao);

        List<Atendimento> atendimentos = atendimentoRepository.findByPacienteIdOrderByDataAtendimentoDesc(pacienteId);

        if (atendimentos.isEmpty()) {
            Paragraph vazio = new Paragraph("Nenhum atendimento cadastrado.", normal);
            vazio.setSpacingAfter(18f);
            document.add(vazio);
            return;
        }

        for (Atendimento atendimento : atendimentos) {
            PdfPTable tabela = criarBlocoAtendimento(atendimento, normalNegrito, normal);
            garantirEspaco(document, writer, 150f);
            document.add(tabela);
        }
    }

    private void garantirEspaco(
            Document document,
            PdfWriter writer,
            float espacoNecessario) throws DocumentException {

        float posicaoAtual = writer.getVerticalPosition(false);

        if (posicaoAtual < espacoNecessario) {
            document.newPage();
        }
    }

    private PdfPTable criarBlocoAtendimento(
            Atendimento atendimento,
            Font normalNegrito,
            Font normal) throws DocumentException {

        PdfPTable tabela = new PdfPTable(1);
        tabela.setWidthPercentage(100);
        tabela.setKeepTogether(true);
        tabela.setSplitRows(false);
        tabela.setSpacingAfter(14f);

        String data = atendimento.getDataAtendimento() == null
                ? "-"
                : atendimento.getDataAtendimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        adicionarLinhaAtendimento(tabela, "Data:", data, normalNegrito, normal);
        adicionarLinhaAtendimento(tabela, "Queixa principal:", valor(atendimento.getQueixaPrincipal()),
                normalNegrito, normal);
        adicionarLinhaAtendimento(tabela, "Evolução clínica:", valor(atendimento.getEvolucaoClinica()),
                normalNegrito, normal);
        adicionarLinhaAtendimento(tabela, "Procedimento:", valor(atendimento.getProcedimentoRealizado()),
                normalNegrito, normal);
        adicionarLinhaAtendimento(tabela, "Observações:", valor(atendimento.getObservacoes()), normalNegrito, normal);

        return tabela;
    }

    private void adicionarDataAssinaturaNoFinal(
            Document document,
            PdfWriter writer,
            Usuario dentista,
            Font normal,
            Font assinaturaNome,
            Font assinaturaDados) throws DocumentException {

        PdfContentByte canvas = writer.getDirectContent();

        Font assinaturaNomeMenor = new Font(Font.HELVETICA, 11, Font.BOLD, AZUL_ESCURO);
        Font assinaturaDadosMenor = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
        Font dataNegrito = new Font(Font.HELVETICA, 9, Font.BOLD, AZUL_ESCURO);

        escreverData(canvas, normal, dataNegrito, 195);

        canvas.setColorStroke(Color.BLACK);
        canvas.setLineWidth(1f);
        canvas.moveTo(205, 152);
        canvas.lineTo(390, 152);
        canvas.stroke();

        escrever(canvas, valor(dentista.getNome()), assinaturaNomeMenor, 297, 136, Element.ALIGN_CENTER);

        if (dentista.getEspecialidade() != null && !dentista.getEspecialidade().isBlank()) {
            escrever(canvas, dentista.getEspecialidade(), assinaturaDadosMenor, 297, 120, Element.ALIGN_CENTER);
        }

        if (dentista.getCro() != null && !dentista.getCro().isBlank()) {
            escrever(canvas, dentista.getCro(), assinaturaDadosMenor, 297, 105, Element.ALIGN_CENTER);
        }
    }

    private void adicionarTituloSecao(Document document, String titulo, Font secao) throws DocumentException {
        PdfPTable tabela = new PdfPTable(1);
        tabela.setWidthPercentage(100);
        tabela.setSpacingBefore(14f);
        tabela.setSpacingAfter(14f);

        PdfPCell cell = new PdfPCell(new Phrase(titulo, secao));
        cell.setBorder(Rectangle.LEFT);
        cell.setBorderColor(AZUL_CLARO);
        cell.setBorderWidthLeft(4f);
        cell.setPaddingLeft(10f);
        cell.setPaddingTop(6f);
        cell.setPaddingBottom(6f);
        cell.setBackgroundColor(new Color(248, 251, 253));

        tabela.addCell(cell);
        document.add(tabela);
    }

    private void adicionarBlocoTexto(
            Document document,
            String rotulo,
            String texto,
            Font normalNegrito,
            Font normal) throws DocumentException {

        Paragraph p = new Paragraph();
        p.add(new Chunk(rotulo + " ", normalNegrito));
        p.add(new Chunk(valor(texto), normal));
        p.setSpacingAfter(12f);
        document.add(p);
    }

    private void adicionarCelulaRotuloValor(
            PdfPTable tabela,
            String rotulo,
            String valor,
            Font fonteRotulo,
            Font fonteValor) {

        Phrase phrase = new Phrase();
        phrase.add(new Chunk(rotulo + " ", fonteRotulo));
        phrase.add(new Chunk(valor(valor), fonteValor));

        PdfPCell cell = new PdfPCell(phrase);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(4f);
        tabela.addCell(cell);
    }

    private void adicionarCabecalhoTabela(PdfPTable tabela, String texto, Font fonte) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, fonte));
        cell.setBackgroundColor(new Color(241, 245, 249));
        cell.setBorderColor(new Color(203, 213, 225));
        cell.setPadding(8f);
        tabela.addCell(cell);
    }

    private void adicionarCelulaTabela(PdfPTable tabela, String texto, Font fonte) {
        PdfPCell cell = new PdfPCell(new Phrase(valor(texto), fonte));
        cell.setBorderColor(new Color(203, 213, 225));
        cell.setPadding(8f);
        tabela.addCell(cell);
    }

    private void adicionarLinhaAtendimento(
            PdfPTable tabela,
            String rotulo,
            String texto,
            Font fonteRotulo,
            Font fonteValor) {

        Phrase phrase = new Phrase();
        phrase.add(new Chunk(rotulo + " ", fonteRotulo));
        phrase.add(new Chunk(valor(texto), fonteValor));

        PdfPCell cell = new PdfPCell(phrase);
        cell.setPadding(8f);
        tabela.addCell(cell);
    }

    private PdfPTable criarTabelaOdontogramaSimplificado(List<Odontograma> registros) throws DocumentException {
        PdfPTable wrapper = new PdfPTable(1);
        wrapper.setWidthPercentage(100);
        wrapper.setSpacingAfter(12f);

        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(6f);
        cell.addElement(criarLinhaTituloOdontograma("Superior esquerdo", "Superior direito"));
        cell.addElement(criarLinhaDentes(new int[] { 18, 17, 16, 15, 14, 13, 12, 11 },
                new int[] { 21, 22, 23, 24, 25, 26, 27, 28 }, registros));
        cell.addElement(criarLinhaTituloOdontograma("Inferior esquerdo", "Inferior direito"));
        cell.addElement(criarLinhaDentes(new int[] { 48, 47, 46, 45, 44, 43, 42, 41 },
                new int[] { 31, 32, 33, 34, 35, 36, 37, 38 }, registros));

        wrapper.addCell(cell);

        return wrapper;
    }

    private PdfPTable criarLinhaTituloOdontograma(String esquerda, String direita) throws DocumentException {
        Font fonte = new Font(Font.HELVETICA, 8, Font.BOLD, AZUL_MEDIO);

        PdfPTable tabela = new PdfPTable(2);
        tabela.setWidthPercentage(100);
        tabela.setWidths(new float[] { 1f, 1f });

        PdfPCell cellEsquerda = new PdfPCell(new Phrase(esquerda, fonte));
        cellEsquerda.setBorder(Rectangle.NO_BORDER);
        cellEsquerda.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellEsquerda.setPaddingBottom(5f);
        tabela.addCell(cellEsquerda);

        PdfPCell cellDireita = new PdfPCell(new Phrase(direita, fonte));
        cellDireita.setBorder(Rectangle.NO_BORDER);
        cellDireita.setHorizontalAlignment(Element.ALIGN_CENTER);
        cellDireita.setPaddingBottom(5f);
        tabela.addCell(cellDireita);

        return tabela;
    }

    private PdfPTable criarLinhaDentes(
            int[] dentesEsquerda,
            int[] dentesDireita,
            List<Odontograma> registros) throws DocumentException {

        PdfPTable tabela = new PdfPTable(16);
        tabela.setWidthPercentage(100);

        for (int dente : dentesEsquerda) {
            adicionarDenteTabela(tabela, dente, registros);
        }

        for (int dente : dentesDireita) {
            adicionarDenteTabela(tabela, dente, registros);
        }

        tabela.setSpacingAfter(12f);

        return tabela;
    }

    private void adicionarDenteTabela(PdfPTable tabela, int numeroDente, List<Odontograma> registros) {
        boolean possuiRegistro = possuiRegistroDente(registros, numeroDente);

        Font fonte = new Font(Font.HELVETICA, 8, Font.BOLD, Color.BLACK);

        PdfPCell cell = new PdfPCell(new Phrase(String.valueOf(numeroDente), fonte));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6f);
        cell.setBackgroundColor(possuiRegistro ? new Color(255, 235, 235) : Color.WHITE);
        cell.setBorderColor(possuiRegistro ? Color.RED : new Color(190, 205, 220));
        tabela.addCell(cell);
    }

    private class DocumentoClinicoPageEvent extends PdfPageEventHelper {

        private final String titulo;

        DocumentoClinicoPageEvent(String titulo) {
            this.titulo = titulo;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte under = writer.getDirectContentUnder();
            desenharMarcaDaguaDente(under);

            PdfContentByte canvas = writer.getDirectContent();

            desenharBarraLateralDireita(canvas);
            desenharDetalheInferiorEsquerdo(canvas);

            Font tituloCabecalho = new Font(Font.HELVETICA, 21, Font.BOLD, AZUL_ESCURO);
            Font subtitulo = new Font(Font.HELVETICA, 10, Font.NORMAL, CINZA_TEXTO);
            Font tituloFonte = new Font(Font.HELVETICA, 15, Font.BOLD, AZUL_ESCURO);
            Font rodape = new Font(Font.HELVETICA, 9, Font.NORMAL, AZUL_MEDIO);

            escreverCabecalho(canvas, tituloCabecalho, subtitulo);

            if (writer.getPageNumber() == 1) {
                escreverTitulo(canvas, titulo, tituloFonte);
            }

            escreverRodape(canvas, rodape);

            Font pagina = new Font(Font.HELVETICA, 8, Font.NORMAL, CINZA_TEXTO);
            escrever(canvas, "Página " + writer.getPageNumber(), pagina, 505, 82, Element.ALIGN_RIGHT);
        }
    }

    private class ManualProntuarioContext {

        private static final float X_INICIO = 90f;
        private static final float LARGURA_CONTEUDO = 415f;
        private static final float Y_INICIAL_PRIMEIRA_PAGINA = 590f;
        private static final float Y_INICIAL_DEMAIS_PAGINAS = 610f;
        private static final float LIMITE_INFERIOR_CONTEUDO = 225f;

        private final Document document;
        private final PdfWriter writer;
        private final String titulo;
        private int numeroPagina = 0;
        private float yAtual;

        ManualProntuarioContext(Document document, PdfWriter writer, String titulo) {
            this.document = document;
            this.writer = writer;
            this.titulo = titulo;
        }

        void iniciarPrimeiraPagina() {
            numeroPagina = 1;
            prepararPagina(true);
        }

        void novaPagina() throws DocumentException {
            document.newPage();
            numeroPagina++;
            prepararPagina(false);
        }

        void adicionarBloco(PdfPTable bloco, float espacamentoAntes, float espacamentoDepois) throws DocumentException {
            bloco.setTotalWidth(LARGURA_CONTEUDO);
            bloco.setLockedWidth(true);
            bloco.calculateHeights(false);

            float altura = bloco.getTotalHeight();
            float yComEspacamento = yAtual - espacamentoAntes;

            if (yComEspacamento - altura < LIMITE_INFERIOR_CONTEUDO) {
                novaPagina();
                yComEspacamento = yAtual;
            }

            bloco.writeSelectedRows(0, -1, X_INICIO, yComEspacamento, writer.getDirectContent());

            yAtual = yComEspacamento - altura - espacamentoDepois;
        }

        void desenharAssinatura(Usuario dentista) throws DocumentException {
            PdfContentByte canvas = writer.getDirectContent();

            Font normal = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
            Font dataNegrito = new Font(Font.HELVETICA, 9, Font.BOLD, AZUL_ESCURO);
            Font assinaturaNome = new Font(Font.HELVETICA, 11, Font.BOLD, AZUL_ESCURO);
            Font assinaturaDados = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);

            escreverData(canvas, normal, dataNegrito, 195);

            canvas.setColorStroke(Color.BLACK);
            canvas.setLineWidth(1f);
            canvas.moveTo(205, 152);
            canvas.lineTo(390, 152);
            canvas.stroke();

            escrever(canvas, valor(dentista.getNome()), assinaturaNome, 297, 136, Element.ALIGN_CENTER);

            if (dentista.getEspecialidade() != null && !dentista.getEspecialidade().isBlank()) {
                escrever(canvas, dentista.getEspecialidade(), assinaturaDados, 297, 120, Element.ALIGN_CENTER);
            }

            if (dentista.getCro() != null && !dentista.getCro().isBlank()) {
                escrever(canvas, dentista.getCro(), assinaturaDados, 297, 105, Element.ALIGN_CENTER);
            }
        }

        private void prepararPagina(boolean primeiraPagina) {
            PdfContentByte under = writer.getDirectContentUnder();
            desenharMarcaDaguaDente(under);

            PdfContentByte canvas = writer.getDirectContent();

            desenharBarraLateralDireita(canvas);
            desenharDetalheInferiorEsquerdo(canvas);

            Font tituloCabecalho = new Font(Font.HELVETICA, 21, Font.BOLD, AZUL_ESCURO);
            Font subtitulo = new Font(Font.HELVETICA, 10, Font.NORMAL, CINZA_TEXTO);
            Font tituloFonte = new Font(Font.HELVETICA, 15, Font.BOLD, AZUL_ESCURO);
            Font rodape = new Font(Font.HELVETICA, 9, Font.NORMAL, AZUL_MEDIO);
            Font pagina = new Font(Font.HELVETICA, 8, Font.NORMAL, CINZA_TEXTO);

            escreverCabecalho(canvas, tituloCabecalho, subtitulo);

            if (primeiraPagina) {
                escreverTitulo(canvas, titulo, tituloFonte);
                yAtual = Y_INICIAL_PRIMEIRA_PAGINA;
            } else {
                yAtual = Y_INICIAL_DEMAIS_PAGINAS;
            }

            escreverRodape(canvas, rodape);
            escrever(canvas, "Página " + numeroPagina, pagina, 505, 82, Element.ALIGN_RIGHT);
        }
    }

    private void adicionarDadosPacienteManual(
            ManualProntuarioContext contexto,
            Paciente paciente) throws DocumentException {

        Font secao = new Font(Font.HELVETICA, 12, Font.BOLD, AZUL_ESCURO);
        Font normal = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);

        PdfPTable bloco = criarBlocoManual();

        PdfPCell cell = criarCelulaBlocoManual();

        Paragraph titulo = new Paragraph("DADOS DO PACIENTE", secao);
        titulo.setSpacingAfter(12f);
        cell.addElement(titulo);

        Paragraph nome = new Paragraph("Nome: " + valor(paciente.getNome()), normal);
        nome.setSpacingAfter(3f);
        cell.addElement(nome);

        Paragraph cpf = new Paragraph("CPF: " + valor(paciente.getCpf()), normal);
        cell.addElement(cpf);

        bloco.addCell(cell);

        contexto.adicionarBloco(bloco, 0f, 18f);
    }

    private void adicionarSecaoAnamneseManual(
            ManualProntuarioContext contexto,
            Long pacienteId) throws DocumentException {

        Font secao = new Font(Font.HELVETICA, 12, Font.BOLD, AZUL_ESCURO);
        Font normal = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
        Font normalNegrito = new Font(Font.HELVETICA, 10, Font.BOLD, Color.BLACK);

        PdfPTable bloco = criarBlocoManual();
        PdfPCell cell = criarCelulaBlocoManual();

        cell.addElement(criarTituloSecaoTabela("ANAMNESE", secao));

        Anamnese anamnese = anamneseRepository.findByPacienteId(pacienteId).orElse(null);

        if (anamnese == null) {
            cell.addElement(new Paragraph("Nenhuma anamnese cadastrada.", normal));
            bloco.addCell(cell);
            contexto.adicionarBloco(bloco, 0f, 12f);
            return;
        }

        PdfPTable tabela = new PdfPTable(2);
        tabela.setWidthPercentage(100);
        tabela.setWidths(new float[] { 1f, 1f });
        tabela.setSpacingAfter(8f);

        adicionarCelulaRotuloValor(tabela, "Hipertensão:", simNao(anamnese.getHipertensao()), normalNegrito, normal);
        adicionarCelulaRotuloValor(tabela, "Diabetes:", simNao(anamnese.getDiabetes()), normalNegrito, normal);
        adicionarCelulaRotuloValor(tabela, "Fumante:", simNao(anamnese.getFumante()), normalNegrito, normal);
        adicionarCelulaRotuloValor(tabela, "Grávida:", simNao(anamnese.getGravida()), normalNegrito, normal);

        cell.addElement(tabela);
        cell.addElement(criarParagrafoRotuloValor("Alergias:", valor(anamnese.getAlergias()), normalNegrito, normal));
        cell.addElement(
                criarParagrafoRotuloValor("Medicamentos:", valor(anamnese.getMedicamentos()), normalNegrito, normal));
        cell.addElement(
                criarParagrafoRotuloValor("Observações:", valor(anamnese.getObservacoes()), normalNegrito, normal));

        bloco.addCell(cell);
        contexto.adicionarBloco(bloco, 0f, 12f);
    }

    private void adicionarSecaoOdontogramaManual(
            ManualProntuarioContext contexto,
            Long pacienteId) throws DocumentException {

        Font secao = new Font(Font.HELVETICA, 12, Font.BOLD, AZUL_ESCURO);
        Font normal = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
        Font normalNegrito = new Font(Font.HELVETICA, 10, Font.BOLD, Color.BLACK);

        List<Odontograma> registros = odontogramaRepository.findByPacienteIdOrderByNumeroDenteAsc(pacienteId);

        PdfPTable bloco = criarBlocoManual();
        PdfPCell cell = criarCelulaBlocoManual();

        cell.addElement(criarTituloSecaoTabela("ODONTOGRAMA", secao));
        cell.addElement(criarTabelaOdontogramaSimplificado(registros));

        if (registros.isEmpty()) {
            Paragraph vazio = new Paragraph("Nenhum registro de odontograma cadastrado.", normal);
            vazio.setSpacingBefore(8f);
            cell.addElement(vazio);
        } else {
            Paragraph subtitulo = new Paragraph("REGISTROS DO ODONTOGRAMA", normalNegrito);
            subtitulo.setSpacingBefore(10f);
            subtitulo.setSpacingAfter(8f);
            cell.addElement(subtitulo);

            PdfPTable tabela = new PdfPTable(3);
            tabela.setWidthPercentage(100);
            tabela.setSplitRows(false);
            tabela.setWidths(new float[] { 0.8f, 1.4f, 3.8f });

            adicionarCabecalhoTabela(tabela, "Dente", normalNegrito);
            adicionarCabecalhoTabela(tabela, "Status", normalNegrito);
            adicionarCabecalhoTabela(tabela, "Observação", normalNegrito);

            for (Odontograma registro : registros) {
                adicionarCelulaTabela(tabela, valor(registro.getNumeroDente()), normal);
                adicionarCelulaTabela(tabela, valor(registro.getStatus()), normal);
                adicionarCelulaTabela(tabela, valor(registro.getObservacao()), normal);
            }

            cell.addElement(tabela);
        }

        bloco.addCell(cell);
        contexto.adicionarBloco(bloco, 0f, 12f);
    }

    private void adicionarSecaoAtendimentosManual(
            ManualProntuarioContext contexto,
            Long pacienteId) throws DocumentException {

        Font secao = new Font(Font.HELVETICA, 12, Font.BOLD, AZUL_ESCURO);
        Font normal = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
        Font normalNegrito = new Font(Font.HELVETICA, 10, Font.BOLD, Color.BLACK);

        List<Atendimento> atendimentos = atendimentoRepository.findByPacienteIdOrderByDataAtendimentoDesc(pacienteId);

        if (atendimentos.isEmpty()) {
            PdfPTable bloco = criarBlocoManual();
            PdfPCell cell = criarCelulaBlocoManual();

            cell.addElement(criarTituloSecaoTabela("ATENDIMENTOS", secao));
            cell.addElement(new Paragraph("Nenhum atendimento cadastrado.", normal));

            bloco.addCell(cell);
            contexto.adicionarBloco(bloco, 0f, 12f);
            return;
        }

        for (int i = 0; i < atendimentos.size(); i++) {
            PdfPTable bloco = criarBlocoManual();
            PdfPCell cell = criarCelulaBlocoManual();

            if (i == 0) {
                cell.addElement(criarTituloSecaoTabela("ATENDIMENTOS", secao));
            }

            cell.addElement(criarBlocoAtendimento(atendimentos.get(i), normalNegrito, normal));

            bloco.addCell(cell);
            contexto.adicionarBloco(bloco, 0f, 12f);
        }
    }

    private PdfPTable criarBlocoManual() {
        PdfPTable bloco = new PdfPTable(1);
        bloco.setWidthPercentage(100);
        bloco.setKeepTogether(true);
        bloco.setSplitRows(false);
        return bloco;
    }

    private PdfPCell criarCelulaBlocoManual() {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(0f);
        return cell;
    }

    private PdfPTable criarTituloSecaoTabela(String titulo, Font secao) {
        PdfPTable tabela = new PdfPTable(1);
        tabela.setWidthPercentage(100);
        tabela.setSpacingAfter(10f);

        PdfPCell cell = new PdfPCell(new Phrase(titulo, secao));
        cell.setBorder(Rectangle.LEFT);
        cell.setBorderColor(AZUL_CLARO);
        cell.setBorderWidthLeft(4f);
        cell.setPaddingLeft(10f);
        cell.setPaddingTop(5f);
        cell.setPaddingBottom(5f);
        cell.setBackgroundColor(new Color(248, 251, 253));

        tabela.addCell(cell);

        return tabela;
    }

    private Paragraph criarParagrafoRotuloValor(
            String rotulo,
            String texto,
            Font normalNegrito,
            Font normal) {

        Paragraph p = new Paragraph();
        p.add(new Chunk(rotulo + " ", normalNegrito));
        p.add(new Chunk(valor(texto), normal));
        p.setSpacingAfter(8f);
        return p;
    }

    private void adicionarDadosOrcamentoPdf(
            Document document,
            Orcamento orcamento,
            Font secao,
            Font normal,
            Font normalNegrito) throws DocumentException {

        adicionarTituloSecao(document, "DADOS DO ORÇAMENTO", secao);

        PdfPTable tabela = new PdfPTable(2);
        tabela.setWidthPercentage(100);
        tabela.setWidths(new float[] { 1f, 1f });
        tabela.setSpacingAfter(14f);

        adicionarCelulaRotuloValor(tabela, "Paciente:", valor(orcamento.getPaciente().getNome()), normalNegrito,
                normal);
        adicionarCelulaRotuloValor(tabela, "CPF:", valor(orcamento.getPaciente().getCpf()), normalNegrito, normal);
        adicionarCelulaRotuloValor(tabela, "Dentista:", valor(orcamento.getUsuario().getNome()), normalNegrito, normal);
        adicionarCelulaRotuloValor(tabela, "Data:", formatarDataOrcamento(orcamento.getDataCriacao()),
                normalNegrito, normal);
        adicionarCelulaRotuloValor(tabela, "Validade:", valorValidade(orcamento.getValidadeDias()), normalNegrito,
                normal);
        adicionarCelulaRotuloValor(tabela, "Status:", formatarStatusOrcamento(orcamento.getStatus()), normalNegrito,
                normal);

        document.add(tabela);
    }

    private void adicionarItensOrcamentoPdf(
            Document document,
            Orcamento orcamento,
            Font normal,
            Font normalNegrito) throws DocumentException {

        adicionarTituloSecao(document, "ITENS DO ORÇAMENTO", normalNegrito);

        Font tabelaFonte = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.BLACK);
        Font tabelaFonteNegrito = new Font(Font.HELVETICA, 8, Font.BOLD, Color.BLACK);

        PdfPTable tabela = new PdfPTable(5);
        tabela.setWidthPercentage(100);
        tabela.setWidths(new float[] { 2.4f, 4.2f, 0.6f, 1.9f, 1.9f });
        tabela.setSpacingAfter(14f);

        adicionarCabecalhoTabela(tabela, "Dente(s)", tabelaFonteNegrito);
        adicionarCabecalhoTabela(tabela, "Procedimento", tabelaFonteNegrito);
        adicionarCabecalhoTabelaCentralizada(tabela, "Qtd.", tabelaFonteNegrito);
        adicionarCabecalhoTabela(tabela, "Valor Unit.", tabelaFonteNegrito);
        adicionarCabecalhoTabela(tabela, "Subtotal", tabelaFonteNegrito);

        if (orcamento.getItens() == null || orcamento.getItens().isEmpty()) {
            PdfPCell cell = new PdfPCell(new Phrase("Nenhum item cadastrado.", tabelaFonte));
            cell.setColspan(5);
            cell.setPadding(8f);
            tabela.addCell(cell);
        } else {
            for (OrcamentoItem item : orcamento.getItens()) {
                adicionarCelulaTabelaSemQuebra(tabela, formatarDentesOrcamento(item.getDentes()), tabelaFonte);
                adicionarCelulaTabela(tabela, valor(item.getProcedimento()), tabelaFonte);
                adicionarCelulaTabelaCentralizadaSemQuebra(tabela,
                        item.getQuantidade() == null ? "-" : item.getQuantidade().toString(), tabelaFonte);
                adicionarCelulaTabelaDireitaSemQuebra(tabela, formatarMoeda(item.getValorUnitario()), tabelaFonte);
                adicionarCelulaTabelaDireitaSemQuebra(tabela, formatarMoeda(item.getSubtotal()), tabelaFonte);
            }
        }

        document.add(tabela);
    }

    private void adicionarResumoOrcamentoPdf(
            Document document,
            Orcamento orcamento,
            Font normal,
            Font normalNegrito) throws DocumentException {

        PdfPTable tabela = new PdfPTable(2);
        tabela.setWidthPercentage(45);
        tabela.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tabela.setWidths(new float[] { 1.3f, 1f });
        tabela.setSpacingAfter(16f);

        adicionarLinhaResumoOrcamento(tabela, "Subtotal:", formatarMoeda(orcamento.getSubtotal()), normal,
                normalNegrito);
        adicionarLinhaResumoOrcamento(tabela, "Desconto:", formatarMoeda(orcamento.getDesconto()), normal,
                normalNegrito);
        adicionarLinhaResumoOrcamento(tabela, "Total:", formatarMoeda(orcamento.getTotal()), normalNegrito,
                normalNegrito);

        document.add(tabela);
    }

    private void adicionarPagamentosOrcamentoPdf(
            Document document,
            Orcamento orcamento,
            Font normal,
            Font normalNegrito) throws DocumentException {

        List<OrcamentoPagamento> pagamentos = orcamentoPagamentoRepository
                .findByOrcamentoIdOrderByDataPagamentoDesc(orcamento.getId());

        if (pagamentos.isEmpty()) {
            return;
        }

        Font tituloSecao = new Font(Font.HELVETICA, 12, Font.BOLD, AZUL_ESCURO);
        Font tabelaFonte = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.BLACK);
        Font tabelaFonteNegrito = new Font(Font.HELVETICA, 8, Font.BOLD, Color.BLACK);

        PdfPTable bloco = new PdfPTable(1);
        bloco.setWidthPercentage(100);
        bloco.setKeepTogether(true);
        bloco.setSplitRows(false);
        bloco.setSpacingBefore(14f);
        bloco.setSpacingAfter(16f);

        PdfPCell cellBloco = new PdfPCell();
        cellBloco.setBorder(Rectangle.NO_BORDER);
        cellBloco.setPadding(0f);

        PdfPTable titulo = new PdfPTable(1);
        titulo.setWidthPercentage(100);
        titulo.setSpacingAfter(14f);

        PdfPCell cellTitulo = new PdfPCell(new Phrase("PAGAMENTOS RECEBIDOS", tituloSecao));
        cellTitulo.setBorder(Rectangle.LEFT);
        cellTitulo.setBorderColor(AZUL_CLARO);
        cellTitulo.setBorderWidthLeft(4f);
        cellTitulo.setPaddingLeft(10f);
        cellTitulo.setPaddingTop(6f);
        cellTitulo.setPaddingBottom(6f);
        cellTitulo.setBackgroundColor(new Color(248, 251, 253));
        titulo.addCell(cellTitulo);

        cellBloco.addElement(titulo);

        PdfPTable tabela = new PdfPTable(4);
        tabela.setWidthPercentage(100);
        tabela.setWidths(new float[] { 1.35f, 1.35f, 2.7f, 1.3f });
        tabela.setSpacingAfter(14f);
        tabela.setKeepTogether(true);
        tabela.setSplitRows(false);

        adicionarCabecalhoTabelaSemQuebra(tabela, "Data", tabelaFonteNegrito);
        adicionarCabecalhoTabelaSemQuebra(tabela, "Forma", tabelaFonteNegrito);
        adicionarCabecalhoTabela(tabela, "Observação", tabelaFonteNegrito);
        adicionarCabecalhoTabelaSemQuebra(tabela, "Valor", tabelaFonteNegrito);

        for (OrcamentoPagamento pagamento : pagamentos) {
            adicionarCelulaTabelaSemQuebra(tabela, formatarDataHoraOrcamento(pagamento.getDataPagamento()),
                    tabelaFonte);
            adicionarCelulaTabelaSemQuebra(tabela, formatarFormaPagamento(pagamento.getFormaPagamento()), tabelaFonte);
            adicionarCelulaTabela(tabela, valor(pagamento.getObservacao()), tabelaFonte);
            adicionarCelulaTabelaDireitaSemQuebra(tabela, formatarMoeda(pagamento.getValorPago()), tabelaFonte);
        }

        cellBloco.addElement(tabela);

        PdfPTable resumo = new PdfPTable(2);
        resumo.setWidthPercentage(45);
        resumo.setHorizontalAlignment(Element.ALIGN_RIGHT);
        resumo.setWidths(new float[] { 1.3f, 1f });

        adicionarLinhaResumoOrcamento(
                resumo,
                "Total pago:",
                formatarMoeda(calcularTotalPagoOrcamento(orcamento)),
                normal,
                normalNegrito);

        adicionarLinhaResumoOrcamento(
                resumo,
                "Saldo devedor:",
                formatarMoeda(calcularSaldoDevedorOrcamento(orcamento)),
                normalNegrito,
                normalNegrito);

        cellBloco.addElement(resumo);

        bloco.addCell(cellBloco);

        document.add(bloco);
    }

    private void adicionarObservacoesOrcamentoPdf(
            Document document,
            Orcamento orcamento,
            Font secao,
            Font normal) throws DocumentException {

        adicionarTituloSecao(document, "OBSERVAÇÕES", secao);

        Paragraph observacoes = new Paragraph(valor(orcamento.getObservacoes()), normal);
        observacoes.setSpacingAfter(16f);
        document.add(observacoes);
    }

    private void adicionarLinhaResumoOrcamento(
            PdfPTable tabela,
            String rotulo,
            String valor,
            Font fonteRotulo,
            Font fonteValor) {

        PdfPCell cellRotulo = new PdfPCell(new Phrase(rotulo, fonteRotulo));
        cellRotulo.setBorder(Rectangle.NO_BORDER);
        cellRotulo.setPadding(5f);
        cellRotulo.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tabela.addCell(cellRotulo);

        PdfPCell cellValor = new PdfPCell(new Phrase(valor, fonteValor));
        cellValor.setBorder(Rectangle.NO_BORDER);
        cellValor.setPadding(5f);
        cellValor.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tabela.addCell(cellValor);
    }

    private void adicionarCelulaTabelaDireita(PdfPTable tabela, String texto, Font fonte) {
        PdfPCell cell = new PdfPCell(new Phrase(valor(texto), fonte));
        cell.setBorderColor(new Color(203, 213, 225));
        cell.setPadding(8f);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        tabela.addCell(cell);
    }

    private void adicionarCelulaTabelaCentralizadaSemQuebra(PdfPTable tabela, String texto, Font fonte) {
        PdfPCell cell = new PdfPCell(new Phrase(valor(texto), fonte));
        cell.setBorderColor(new Color(203, 213, 225));
        cell.setPadding(7f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setNoWrap(true);
        tabela.addCell(cell);
    }

    private void adicionarCelulaTabelaCentralizada(PdfPTable tabela, String texto, Font fonte) {
        PdfPCell cell = new PdfPCell(new Phrase(valor(texto), fonte));
        cell.setBorderColor(new Color(203, 213, 225));
        cell.setPadding(8f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabela.addCell(cell);
    }

    private String formatarMoeda(BigDecimal valor) {
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR"))
                .format(valor == null ? BigDecimal.ZERO : valor);
    }

    private BigDecimal calcularTotalPagoOrcamento(Orcamento orcamento) {
        return orcamentoPagamentoRepository.findByOrcamentoIdOrderByDataPagamentoDesc(orcamento.getId())
                .stream()
                .map(OrcamentoPagamento::getValorPago)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularSaldoDevedorOrcamento(Orcamento orcamento) {
        BigDecimal saldo = (orcamento.getTotal() == null ? BigDecimal.ZERO : orcamento.getTotal())
                .subtract(calcularTotalPagoOrcamento(orcamento));

        return saldo.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : saldo;
    }

    private String formatarDataHoraOrcamento(java.time.LocalDateTime data) {
        if (data == null) {
            return "-";
        }

        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private String valorValidade(Integer validadeDias) {
        if (validadeDias == null) {
            return "-";
        }

        return validadeDias + " dias";
    }

    private String formatarStatusOrcamento(String status) {
        if (status == null || status.isBlank()) {
            return "-";
        }

        return status.replace("_", " ");
    }

    private String formatarDentesOrcamento(String dentes) {
        if (dentes == null || dentes.isBlank()) {
            return "-";
        }

        return switch (dentes) {
            case "PROTESE_TOTAL_SUPERIOR" -> "Prótese Total Superior";
            case "PROTESE_TOTAL_INFERIOR" -> "Prótese Total Inferior";
            case "PROTESE_TOTAL_COMPLETA" -> "Prótese Total Completa";
            default -> dentes;
        };
    }

    private void validarAtestadoRequest(AtestadoRequest request) {
        if (request == null) {
            throw new RuntimeException("Dados do atestado são obrigatórios");
        }

        if (request.getTexto() == null || request.getTexto().trim().isEmpty()) {
            throw new RuntimeException("Texto do atestado é obrigatório");
        }

        if (request.getTexto().length() > 800) {
            throw new RuntimeException("Texto do atestado muito longo. Limite máximo: 800 caracteres.");
        }
    }

    private void validarReceituarioRequest(ReceituarioRequest request) {
        if (request == null) {
            throw new RuntimeException("Dados do receituário são obrigatórios");
        }

        if (request.getPrescricao() == null || request.getPrescricao().trim().isEmpty()) {
            throw new RuntimeException("Prescrição é obrigatória");
        }

        if (request.getPrescricao().length() > 1200) {
            throw new RuntimeException("Prescrição muito longa. Limite máximo: 1200 caracteres.");
        }
    }

    private void validarProntuarioRequest(ProntuarioPdfRequest request) {
        if (request == null) {
            throw new RuntimeException("Dados do prontuário são obrigatórios");
        }

        if (!Boolean.TRUE.equals(request.getIncluirAnamnese())
                && !Boolean.TRUE.equals(request.getIncluirOdontograma())
                && !Boolean.TRUE.equals(request.getIncluirAtendimentos())) {
            throw new RuntimeException("Selecione ao menos uma informação para gerar o prontuário");
        }
    }

    private void validarDadosDentista(Usuario dentista) {
        if (dentista.getNome() == null || dentista.getNome().isBlank()) {
            throw new RuntimeException("Nome do dentista é obrigatório");
        }

        if (!"DENTISTA".equals(dentista.getPerfil()) && !"ADMIN".equals(dentista.getPerfil())) {
            throw new RuntimeException(
                    "A emissão de documentos clínicos não é permitida para o seu perfil de usuário!");
        }
    }

    private String simNao(Boolean valor) {
        return Boolean.TRUE.equals(valor) ? "Sim" : "Não";
    }

    private String valor(String valor) {
        return valor == null || valor.isBlank() ? "-" : valor;
    }

    private String formatarFormaPagamento(String forma) {
        if (forma == null || forma.isBlank()) {
            return "-";
        }

        return switch (forma) {
            case "PIX" -> "PIX";
            case "DINHEIRO" -> "Dinheiro";
            case "CARTAO_CREDITO" -> "Cartão crédito";
            case "CARTAO_DEBITO" -> "Cartão débito";
            case "TRANSFERENCIA" -> "Transferência";
            case "OUTRO" -> "Outro";
            default -> forma;
        };
    }

    private void adicionarCabecalhoTabelaCentralizada(PdfPTable tabela, String texto, Font fonte) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, fonte));
        cell.setBackgroundColor(new Color(241, 245, 249));
        cell.setBorderColor(new Color(203, 213, 225));
        cell.setPadding(7f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setNoWrap(true);
        tabela.addCell(cell);
    }

    private String formatarDataOrcamento(java.time.LocalDateTime data) {
        if (data == null) {
            return "-";
        }

        return data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private void adicionarCabecalhoTabelaSemQuebra(PdfPTable tabela, String texto, Font fonte) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, fonte));
        cell.setBackgroundColor(new Color(241, 245, 249));
        cell.setBorderColor(new Color(203, 213, 225));
        cell.setPadding(7f);
        cell.setNoWrap(true);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        tabela.addCell(cell);
    }

    private void adicionarCelulaTabelaSemQuebra(PdfPTable tabela, String texto, Font fonte) {
        PdfPCell cell = new PdfPCell(new Phrase(valor(texto), fonte));
        cell.setBorderColor(new Color(203, 213, 225));
        cell.setPadding(7f);
        cell.setNoWrap(true);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        tabela.addCell(cell);
    }

    private void adicionarCelulaTabelaDireitaSemQuebra(PdfPTable tabela, String texto, Font fonte) {
        PdfPCell cell = new PdfPCell(new Phrase(valor(texto), fonte));
        cell.setBorderColor(new Color(203, 213, 225));
        cell.setPadding(7f);
        cell.setNoWrap(true);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        tabela.addCell(cell);
    }
}