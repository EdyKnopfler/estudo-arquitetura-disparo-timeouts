package com.derso.disparotimeouts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.derso.controlesessao.persistencia.SessaoRepositorio;

@Service
public class TimeoutExclusoes {
	
	@Autowired
	private SessaoRepositorio sessaoRepositorio;
	
	@Scheduled(fixedDelayString = "${intervalo-exclusoes}")
	@Transactional
	public void executarExclusoes() {
		System.out.println("Removendo sessões inválidas...");
		sessaoRepositorio.removerInvalidos();
		System.out.println("Sessões inválidas removidas");
	}

}
