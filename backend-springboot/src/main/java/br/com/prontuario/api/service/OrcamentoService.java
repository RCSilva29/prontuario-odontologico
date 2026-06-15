package br.com.prontuario.api.service;

import br.com.prontuario.api.dto.OrcamentoItemRequest;
import br.com.prontuario.api.dto.OrcamentoItemResponse;
import br.com.prontuario.api.dto.OrcamentoPagamentoRequest;
import br.com.prontuario.api.dto.OrcamentoPagamentoResponse;
import br.com.prontuario.api.dto.OrcamentoRequest;
import br.com.prontuario.api.dto.OrcamentoResponse;
import br.com.prontuario.api.entity.Orcamento;
import br.com.prontuario.api.entity.OrcamentoItem;
import br.com.prontuario.api.entity.OrcamentoPagamento;
import br.com.prontuario.api.entity.Paciente;
import br.com.prontuario.api.entity.Usuario;
import br.com.prontuario.api.repository.OrcamentoPagamentoRepository;
import br.com.prontuario.api.repository.OrcamentoRepository;
import br.com.prontuario.api.repository.PacienteRepository;
import br.com.prontuario.api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrcamentoService {

    private final OrcamentoRepository orcamentoRepository;
    private final OrcamentoPagamentoRepository orcamentoPagamentoRepository;
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;

    public OrcamentoService(
            OrcamentoRepository orcamentoRepository,
            OrcamentoPagamentoRepository orcamentoPagamentoRepository,
            PacienteRepository pacienteRepository,
            UsuarioRepository usuarioRepository) {
        this.orcamentoRepository = orcamentoRepository;
        this.orcamentoPagamentoRepository = orcamentoPagamentoRepository;
        this.pacienteRepository = pacienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<OrcamentoResponse> listarPorPaciente(Long pacienteId) {
        return orcamentoRepository.findByPacienteIdOrderByDataCriacaoDesc(pacienteId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public OrcamentoResponse buscarPorId(Long id) {
        Orcamento orcamento = buscarOrcamento(id);
        return toResponse(orcamento);
    }

    @Transactional
    public OrcamentoResponse criar(Long pacienteId, OrcamentoRequest request, String emailUsuarioLogado) {
        Paciente paciente = pacienteRepository.findByIdAndAtivoTrue(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        Usuario usuario = usuarioRepository.findByEmail(emailUsuarioLogado)
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado"));

        validarRequest(request);

        Orcamento orcamento = new Orcamento();
        orcamento.setPaciente(paciente);
        orcamento.setUsuario(usuario);

        preencherOrcamento(orcamento, request);
        atualizarStatusPorPagamento(orcamento);

        return toResponse(orcamentoRepository.save(orcamento));
    }

    @Transactional
    public OrcamentoResponse atualizar(Long id, OrcamentoRequest request) {
        Orcamento orcamento = buscarOrcamento(id);

        validarRequest(request);

        orcamento.getItens().clear();
        preencherOrcamento(orcamento, request);
        atualizarStatusPorPagamento(orcamento);

        return toResponse(orcamentoRepository.save(orcamento));
    }

    @Transactional
    public void excluir(Long id) {
        Orcamento orcamento = buscarOrcamento(id);
        orcamentoRepository.delete(orcamento);
    }

    @Transactional
    public void registrarPagamento(Long orcamentoId, OrcamentoPagamentoRequest request) {
        Orcamento orcamento = buscarOrcamento(orcamentoId);

        validarPagamentoRequest(request);

        OrcamentoPagamento pagamento = new OrcamentoPagamento();
        pagamento.setOrcamento(orcamento);
        pagamento.setDataPagamento(
                request.getDataPagamento() == null ? LocalDateTime.now() : request.getDataPagamento());
        pagamento.setValorPago(request.getValorPago());
        pagamento.setFormaPagamento(request.getFormaPagamento());
        pagamento.setObservacao(request.getObservacao());

        orcamentoPagamentoRepository.save(pagamento);

        atualizarStatusPorPagamento(orcamento);
        orcamentoRepository.save(orcamento);
    }

    @Transactional
    public void excluirPagamento(Long orcamentoId, Long pagamentoId) {
        Orcamento orcamento = buscarOrcamento(orcamentoId);

        OrcamentoPagamento pagamento = orcamentoPagamentoRepository
                .findByIdAndOrcamentoId(pagamentoId, orcamentoId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado para este orçamento"));

        orcamentoPagamentoRepository.delete(pagamento);

        atualizarStatusPorPagamento(orcamento);
        orcamentoRepository.save(orcamento);
    }

    private Orcamento buscarOrcamento(Long id) {
        return orcamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Orçamento não encontrado"));
    }

    private void preencherOrcamento(Orcamento orcamento, OrcamentoRequest request) {
        BigDecimal desconto = nuloParaZero(request.getDesconto());

        orcamento.setValidadeDias(request.getValidadeDias() == null ? 30 : request.getValidadeDias());
        orcamento.setObservacoes(request.getObservacoes());
        orcamento.setDesconto(desconto);

        if ("CANCELADO".equals(request.getStatus())) {
            orcamento.setStatus("CANCELADO");
        } else if (orcamento.getStatus() == null || orcamento.getStatus().isBlank()) {
            orcamento.setStatus("ABERTO");
        }

        for (OrcamentoItemRequest itemRequest : request.getItens()) {
            OrcamentoItem item = criarItem(orcamento, itemRequest);
            orcamento.getItens().add(item);
        }

        BigDecimal subtotal = orcamento.getItens()
                .stream()
                .map(OrcamentoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal total = subtotal.subtract(desconto);

        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        orcamento.setSubtotal(subtotal);
        orcamento.setTotal(total);
    }

    private OrcamentoItem criarItem(Orcamento orcamento, OrcamentoItemRequest request) {
        BigDecimal valorUnitario = nuloParaZero(request.getValorUnitario());
        Integer quantidade = request.getQuantidade() == null ? 1 : request.getQuantidade();
        BigDecimal subtotal = valorUnitario.multiply(BigDecimal.valueOf(quantidade));

        OrcamentoItem item = new OrcamentoItem();
        item.setOrcamento(orcamento);
        item.setCodigo(request.getCodigo());
        item.setDentes(request.getDentes());
        item.setProcedimento(request.getProcedimento());
        item.setQuantidade(quantidade);
        item.setValorUnitario(valorUnitario);
        item.setSubtotal(subtotal);

        return item;
    }

    private void validarRequest(OrcamentoRequest request) {
        if (request == null) {
            throw new RuntimeException("Dados do orçamento são obrigatórios");
        }

        if (request.getItens() == null || request.getItens().isEmpty()) {
            throw new RuntimeException("Inclua ao menos um item no orçamento");
        }

        for (OrcamentoItemRequest item : request.getItens()) {
            if (item.getProcedimento() == null || item.getProcedimento().isBlank()) {
                throw new RuntimeException("Procedimento do item é obrigatório");
            }

            if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
                throw new RuntimeException("Quantidade do item deve ser maior que zero");
            }

            if (item.getValorUnitario() == null || item.getValorUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Valor unitário do item deve ser maior que zero");
            }
        }
    }

    private void validarPagamentoRequest(OrcamentoPagamentoRequest request) {
        if (request == null) {
            throw new RuntimeException("Dados do pagamento são obrigatórios");
        }

        if (request.getValorPago() == null || request.getValorPago().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor pago deve ser maior que zero");
        }
    }

    private BigDecimal calcularTotalPago(Orcamento orcamento) {
        return orcamentoPagamentoRepository.findByOrcamentoIdOrderByDataPagamentoDesc(orcamento.getId())
                .stream()
                .map(OrcamentoPagamento::getValorPago)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularSaldoDevedor(Orcamento orcamento) {
        BigDecimal saldo = nuloParaZero(orcamento.getTotal()).subtract(calcularTotalPago(orcamento));

        if (saldo.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }

        return saldo;
    }

    private void atualizarStatusPorPagamento(Orcamento orcamento) {
        if ("CANCELADO".equals(orcamento.getStatus())) {
            return;
        }

        BigDecimal totalPago = calcularTotalPago(orcamento);
        BigDecimal total = nuloParaZero(orcamento.getTotal());

        if (totalPago.compareTo(BigDecimal.ZERO) == 0) {
            orcamento.setStatus("ABERTO");
            return;
        }

        if (totalPago.compareTo(total) >= 0) {
            orcamento.setStatus("PAGO");
            return;
        }

        orcamento.setStatus("PARCIALMENTE_PAGO");
    }

    private BigDecimal nuloParaZero(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }

    private OrcamentoResponse toResponse(Orcamento orcamento) {
        return new OrcamentoResponse()
                .setId(orcamento.getId())
                .setPacienteId(orcamento.getPaciente().getId())
                .setPacienteNome(orcamento.getPaciente().getNome())
                .setUsuarioId(orcamento.getUsuario().getId())
                .setUsuarioNome(orcamento.getUsuario().getNome())
                .setDataCriacao(orcamento.getDataCriacao())
                .setValidadeDias(orcamento.getValidadeDias())
                .setObservacoes(orcamento.getObservacoes())
                .setSubtotal(orcamento.getSubtotal())
                .setDesconto(orcamento.getDesconto())
                .setTotal(orcamento.getTotal())
                .setTotalPago(calcularTotalPago(orcamento))
                .setSaldoDevedor(calcularSaldoDevedor(orcamento))
                .setStatus(orcamento.getStatus())
                .setItens(
                        orcamento.getItens()
                                .stream()
                                .map(this::toItemResponse)
                                .toList())
                .setPagamentos(
                        orcamentoPagamentoRepository.findByOrcamentoIdOrderByDataPagamentoDesc(orcamento.getId())
                                .stream()
                                .map(this::toPagamentoResponse)
                                .toList());
    }

    private OrcamentoItemResponse toItemResponse(OrcamentoItem item) {
        return new OrcamentoItemResponse()
                .setId(item.getId())
                .setCodigo(item.getCodigo())
                .setDentes(item.getDentes())
                .setProcedimento(item.getProcedimento())
                .setQuantidade(item.getQuantidade())
                .setValorUnitario(item.getValorUnitario())
                .setSubtotal(item.getSubtotal());
    }

    private OrcamentoPagamentoResponse toPagamentoResponse(OrcamentoPagamento pagamento) {
        return new OrcamentoPagamentoResponse()
                .setId(pagamento.getId())
                .setDataPagamento(pagamento.getDataPagamento())
                .setValorPago(pagamento.getValorPago())
                .setFormaPagamento(pagamento.getFormaPagamento())
                .setObservacao(pagamento.getObservacao());
    }
}