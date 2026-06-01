package br.com.prontuario.api.service;

import br.com.prontuario.api.dto.AtestadoRequest;
import br.com.prontuario.api.dto.ReceituarioRequest;
import br.com.prontuario.api.entity.Paciente;
import br.com.prontuario.api.entity.Usuario;
import br.com.prontuario.api.repository.PacienteRepository;
import br.com.prontuario.api.repository.UsuarioRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class DocumentoPdfService {

    private static final Color AZUL_ESCURO = new Color(8, 43, 94);
    private static final Color AZUL_MEDIO = new Color(0, 102, 145);
    private static final Color AZUL_CLARO = new Color(20, 150, 160);
    private static final Color CINZA_TEXTO = new Color(80, 80, 80);
    private static final Color MARCA_DAGUA = new Color(232, 244, 248);

    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;

    public DocumentoPdfService(
            PacienteRepository pacienteRepository,
            UsuarioRepository usuarioRepository) {
        this.pacienteRepository = pacienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public byte[] gerarAtestado(Long pacienteId, AtestadoRequest request, String emailUsuarioLogado) {
        Paciente paciente = buscarPaciente(pacienteId);
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
        Paciente paciente = buscarPaciente(pacienteId);
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

    private Paciente buscarPaciente(Long pacienteId) {
        return pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
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
        desenharDenteEstilizado(
                canvas,
                185,
                535,
                2.45f,
                MARCA_DAGUA,
                MARCA_DAGUA,
                13f,
                6f);
    }

    private void escreverConteudoAtestado(
            PdfContentByte canvas,
            Paciente paciente,
            AtestadoRequest request,
            Usuario dentista) {

        Font tituloCabecalho = new Font(Font.HELVETICA, 21, Font.BOLD, AZUL_ESCURO);
        Font subtitulo = new Font(Font.HELVETICA, 10, Font.NORMAL, CINZA_TEXTO);
        Font titulo = new Font(Font.HELVETICA, 24, Font.BOLD, AZUL_ESCURO);
        Font secao = new Font(Font.HELVETICA, 13, Font.BOLD, AZUL_ESCURO);
        Font normal = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.BLACK);
        Font dataNegrito = new Font(Font.HELVETICA, 11, Font.BOLD, AZUL_ESCURO);
        Font assinaturaNome = new Font(Font.HELVETICA, 14, Font.BOLD, AZUL_ESCURO);
        Font assinaturaDados = new Font(Font.HELVETICA, 13, Font.NORMAL, Color.BLACK);
        Font rodape = new Font(Font.HELVETICA, 9, Font.NORMAL, AZUL_MEDIO);

        escreverCabecalho(canvas, tituloCabecalho, subtitulo);
        escreverTitulo(canvas, "ATESTADO", titulo);

        escrever(canvas, "DADOS DO PACIENTE", secao, 90, 555, Element.ALIGN_LEFT);
        escrever(canvas, "Nome: " + valor(paciente.getNome()), normal, 90, 525, Element.ALIGN_LEFT);
        escrever(canvas, "CPF: " + valor(paciente.getCpf()), normal, 90, 500, Element.ALIGN_LEFT);

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
        Font titulo = new Font(Font.HELVETICA, 24, Font.BOLD, AZUL_ESCURO);
        Font secao = new Font(Font.HELVETICA, 13, Font.BOLD, AZUL_ESCURO);
        Font normal = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.BLACK);
        Font dataNegrito = new Font(Font.HELVETICA, 11, Font.BOLD, AZUL_ESCURO);
        Font assinaturaNome = new Font(Font.HELVETICA, 14, Font.BOLD, AZUL_ESCURO);
        Font assinaturaDados = new Font(Font.HELVETICA, 13, Font.NORMAL, Color.BLACK);
        Font rodape = new Font(Font.HELVETICA, 9, Font.NORMAL, AZUL_MEDIO);

        escreverCabecalho(canvas, tituloCabecalho, subtitulo);
        escreverTitulo(canvas, "RECEITUÁRIO", titulo);

        escrever(canvas, "DADOS DO PACIENTE", secao, 90, 555, Element.ALIGN_LEFT);
        escrever(canvas, "Nome: " + valor(paciente.getNome()), normal, 90, 525, Element.ALIGN_LEFT);
        escrever(canvas, "CPF: " + valor(paciente.getCpf()), normal, 90, 500, Element.ALIGN_LEFT);

        escrever(canvas, "PRESCRIÇÃO", secao, 90, 455, Element.ALIGN_LEFT);
        escreverTextoLongo(canvas, request.getPrescricao(), normal, 90, 430, 505, 305);

        escreverData(canvas, normal, dataNegrito, 230);
        escreverAssinatura(canvas, dentista, assinaturaNome, assinaturaDados);
        escreverRodape(canvas, rodape);
    }

    private void escreverCabecalho(PdfContentByte canvas, Font tituloCabecalho, Font subtitulo) {
        escrever(canvas, "CONSULTÓRIO ODONTOLÓGICO", tituloCabecalho, 120, 765, Element.ALIGN_LEFT);
        escrever(canvas, "Cuidando do seu sorriso com saúde e qualidade", subtitulo, 120, 745, Element.ALIGN_LEFT);
        desenharIconeDente(canvas, 42, 780);
    }

    private void escreverTitulo(PdfContentByte canvas, String texto, Font titulo) {
        escrever(canvas, texto, titulo, 297, 630, Element.ALIGN_CENTER);

        canvas.setColorStroke(AZUL_CLARO);
        canvas.setLineWidth(1f);
        canvas.moveTo(90, 608);
        canvas.lineTo(505, 608);
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

        String especialidade = dentista.getEspecialidade();
        String cro = dentista.getCro();

        escrever(canvas, dentista.getNome(), assinaturaNome, 297, 155, Element.ALIGN_CENTER);

        if (especialidade != null && !especialidade.isBlank()) {
            escrever(canvas, especialidade, assinaturaDados, 297, 132, Element.ALIGN_CENTER);
        }

        if (cro != null && !cro.isBlank()) {
            escrever(canvas, cro, assinaturaDados, 297, 110, Element.ALIGN_CENTER);
        }
    }

    private void escreverRodape(PdfContentByte canvas, Font rodape) {
        escrever(canvas, "Tel.: 21 0000-0000 | 21 99999-9999", rodape, 105, 58, Element.ALIGN_LEFT);
        escrever(canvas, "Rua Dr. Feliciano Sodré, nº 215, sala 204 - Centro, São Gonçalo/RJ", rodape, 105, 36,
                Element.ALIGN_LEFT);
    }

    private void desenharIconeDente(PdfContentByte canvas, float x, float y) {
        desenharDenteEstilizado(
                canvas,
                x,
                y,
                0.52f,
                AZUL_ESCURO,
                AZUL_CLARO,
                4f,
                2.2f);
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
        canvas.curveTo(
                x + 5 * escala, y - 2 * escala,
                x - 5 * escala, y - 25 * escala,
                x + 6 * escala, y - 50 * escala);
        canvas.curveTo(
                x + 18 * escala, y - 80 * escala,
                x + 22 * escala, y - 115 * escala,
                x + 31 * escala, y - 148 * escala);

        canvas.moveTo(x + 22 * escala, y - 8 * escala);
        canvas.curveTo(
                x + 42 * escala, y - 25 * escala,
                x + 70 * escala, y - 25 * escala,
                x + 91 * escala, y - 8 * escala);

        canvas.curveTo(
                x + 112 * escala, y - 25 * escala,
                x + 105 * escala, y - 58 * escala,
                x + 96 * escala, y - 84 * escala);
        canvas.curveTo(
                x + 86 * escala, y - 115 * escala,
                x + 82 * escala, y - 135 * escala,
                x + 78 * escala, y - 148 * escala);

        canvas.moveTo(x + 31 * escala, y - 148 * escala);
        canvas.curveTo(
                x + 42 * escala, y - 122 * escala,
                x + 67 * escala, y - 122 * escala,
                x + 78 * escala, y - 148 * escala);

        canvas.stroke();

        canvas.setColorStroke(corDetalhe);
        canvas.setLineWidth(linhaDetalhe);

        canvas.moveTo(x + 28 * escala, y - 34 * escala);
        canvas.curveTo(
                x + 46 * escala, y - 48 * escala,
                x + 70 * escala, y - 48 * escala,
                x + 88 * escala, y - 34 * escala);

        canvas.stroke();

        canvas.moveTo(x + 14 * escala, y - 108 * escala);
        canvas.curveTo(
                x + 42 * escala, y - 92 * escala,
                x + 75 * escala, y - 82 * escala,
                x + 103 * escala, y - 58 * escala);

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
        ct.setSimpleColumn(new Phrase(texto, fonte), x1, y2, x2, y1, 22, Element.ALIGN_JUSTIFIED);

        try {
            ct.go();
        } catch (DocumentException e) {
            throw new RuntimeException("Erro ao escrever texto no documento", e);
        }
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

        if (request.getOrientacoes() != null && request.getOrientacoes().length() > 500) {
            throw new RuntimeException("Orientações muito longas. Limite máximo: 500 caracteres.");
        }
    }

    private void validarDadosDentista(Usuario dentista) {
        if (dentista.getNome() == null || dentista.getNome().isBlank()) {
            throw new RuntimeException("Nome do dentista é obrigatório");
        }

        if (!"DENTISTA".equals(dentista.getPerfil())) {
            throw new RuntimeException(
                    "A emissão de documentos clínicos é permitida apenas para usuários com perfil DENTISTA");
        }
    }

    private String valor(String valor) {
        return valor == null || valor.isBlank() ? "-" : valor;
    }
}