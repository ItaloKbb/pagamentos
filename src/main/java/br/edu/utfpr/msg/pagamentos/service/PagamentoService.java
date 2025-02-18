package br.edu.utfpr.msg.pagamentos.service;

import br.edu.utfpr.msg.pagamentos.dto.PagtoDTO;
import br.edu.utfpr.msg.pagamentos.dto.PedidoDTO;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PagamentoService {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @RabbitListener(queues = {"pagto-req-queue"})
    public void pagamentoListener(PedidoDTO pedidoDTO) {
        System.out.println("--- Mensagem Recebida: " + pedidoDTO);
        validarPagamento(pedidoDTO);
    }

    public void validarPagamento(PedidoDTO pedidoDTO) {
        PagtoDTO pagtoDTO;

        if (new Random().nextBoolean()) {
            pagtoDTO = new PagtoDTO(
                    "SUCESSO",
                    pedidoDTO
            );
            enviarMensagemSucesso(pagtoDTO);
            System.out.println("Retorno = Sucesso ");
        } else {
            pagtoDTO = new PagtoDTO(
                    "ERRO",
                    pedidoDTO
            );
            enviarMensagemErro(pagtoDTO);
            System.out.println("Retorno = Error ");
        }
        System.out.println(pagtoDTO);
    }

    public void enviarMensagemErro(PagtoDTO pagtoDTO) {
        amqpTemplate.convertAndSend(
                "pagto-exchange",
                "pagto.res.error.key",
                pagtoDTO
        );
    }

    public void enviarMensagemSucesso(PagtoDTO pagtoDTO) {
        amqpTemplate.convertAndSend(
                "pagto-exchange",
                "pagto.res.success.key",
                pagtoDTO
        );
    }
}