package com.derso.disparotimeouts;

import java.time.ZoneId;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.derso.controlesessao.persistencia.EstadoSessao;
import com.derso.controlesessao.persistencia.Sessao;
import com.derso.controlesessao.persistencia.SessaoRepositorio;
import com.derso.controlesessao.persistencia.SessaoServico;
import com.derso.controlesessao.persistencia.TransicaoInvalidaException;

@Service
public class TimeoutTarefa {
	
	private final String exchange = "architecture-studies";
	private final String[] servicos = {"hoteis", "voos"};
	
	@Autowired
	private SessaoRepositorio sessaoRepositorio;
	
	@Autowired
	private SessaoServico sessaoServico;
	
	@Autowired
    private RabbitTemplate rabbitTemplate;
	
	/*
	 * fixedRate = dispara impreterivelmente à taxa indicada
	 * fixedDelay = aguarda o tempo entre uma execução e outra
	 * 
	 * Não queremos que as execuções se sobreponham. Um pool de 2 threads foi alocado.
	 * 
	 * Ref.: https://medium.com/@ali.gelenler/deep-dive-into-spring-schedulers-and-async-methods-27b6586a5a17
	 */
	@Scheduled(fixedDelayString = "${intervalo-timeouts}")
	public void executarTimeouts() {
		List<Sessao> sessoes = sessaoRepositorio.sessoesExpiradasCorrendo();
		
		for (Sessao sessao : sessoes) {
			System.out.println(
					"Sessão expirada em: " + sessao.getExpiracao().atZone(
							ZoneId.of("America/Sao_Paulo")));
			
			try {
				sessaoServico.atualizarEstado(sessao.getUuid(), EstadoSessao.TEMPO_ESGOTADO);
			} catch (TransicaoInvalidaException e) {
				System.out.println("Transição inválida, pulando. Estado atual: " + e.getEstadoAtual());
				continue;
			} catch (Exception e) {
				System.err.println("Impossível atualizar o estado da sessão:");
				e.printStackTrace();
				continue;
			}
			
			for (String servico : servicos) {
				String mensagem = "type=timeout&sessaoUUID=" + sessao.getUuid();
				System.out.println("Enviando " + mensagem);
				rabbitTemplate.convertAndSend(exchange, servico, mensagem);
			}
		}
		
	}

}
