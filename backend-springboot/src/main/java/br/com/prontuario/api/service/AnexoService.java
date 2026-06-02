package br.com.prontuario.api.service;

import br.com.prontuario.api.entity.Anexo;
import br.com.prontuario.api.entity.Paciente;
import br.com.prontuario.api.repository.AnexoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
public class AnexoService {

    private final AnexoRepository repository;
    private final PacienteService pacienteService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public AnexoService(
            AnexoRepository repository,
            PacienteService pacienteService) {
        this.repository = repository;
        this.pacienteService = pacienteService;
    }

    public List<Anexo> listarPorPaciente(
            Long pacienteId,
            String emailUsuarioLogado) {
        pacienteService.buscarPorId(pacienteId, emailUsuarioLogado);
        return repository.findByPacienteIdOrderByDataUploadDesc(pacienteId);
    }

    public Anexo salvar(
            Long pacienteId,
            MultipartFile arquivo,
            String emailUsuarioLogado) {
        try {
            Paciente paciente = pacienteService.buscarPorId(
                    pacienteId,
                    emailUsuarioLogado);

            validarArquivo(arquivo);

            Path pastaPaciente = Path.of(uploadDir, String.valueOf(pacienteId));
            Files.createDirectories(pastaPaciente);

            String nomeArquivo = UUID.randomUUID() + "_" + arquivo.getOriginalFilename();
            Path caminhoArquivo = pastaPaciente.resolve(nomeArquivo);

            arquivo.transferTo(caminhoArquivo.toFile());

            Anexo anexo = new Anexo();
            anexo.setPaciente(paciente);
            anexo.setNomeOriginal(arquivo.getOriginalFilename());
            anexo.setNomeArquivo(nomeArquivo);
            anexo.setTipoConteudo(arquivo.getContentType());
            anexo.setTamanho(arquivo.getSize());
            anexo.setCaminhoArquivo(caminhoArquivo.toString());

            return repository.save(anexo);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar anexo: " + e.getMessage());
        }
    }

    public Anexo buscarPorId(
            Long pacienteId,
            Long anexoId,
            String emailUsuarioLogado) {
        pacienteService.buscarPorId(pacienteId, emailUsuarioLogado);

        Anexo anexo = repository.findById(anexoId)
                .orElseThrow(() -> new RuntimeException("Anexo não encontrado"));

        if (anexo.getPaciente() == null || !pacienteId.equals(anexo.getPaciente().getId())) {
            throw new RuntimeException("Anexo não pertence ao paciente informado");
        }

        return anexo;
    }

    public Resource baixar(
            Long pacienteId,
            Long anexoId,
            String emailUsuarioLogado) {
        Anexo anexo = buscarPorId(
                pacienteId,
                anexoId,
                emailUsuarioLogado);

        return new FileSystemResource(anexo.getCaminhoArquivo());
    }

    public void excluir(
            Long pacienteId,
            Long anexoId,
            String emailUsuarioLogado) {
        try {
            Anexo anexo = buscarPorId(
                    pacienteId,
                    anexoId,
                    emailUsuarioLogado);

            Path caminho = Path.of(anexo.getCaminhoArquivo());
            Files.deleteIfExists(caminho);

            repository.delete(anexo);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir anexo: " + e.getMessage());
        }
    }

    private void validarArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new RuntimeException("Arquivo não informado");
        }

        String tipoConteudo = arquivo.getContentType();

        if (tipoConteudo == null ||
                (!tipoConteudo.equals("application/pdf") &&
                        !tipoConteudo.equals("image/png") &&
                        !tipoConteudo.equals("image/jpeg") &&
                        !tipoConteudo.equals("text/plain"))) {
            throw new RuntimeException("Tipo de arquivo não permitido");
        }
    }
}