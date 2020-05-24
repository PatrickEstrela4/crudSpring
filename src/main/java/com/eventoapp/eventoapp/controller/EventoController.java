package com.eventoapp.eventoapp.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eventoapp.eventoapp.model.Convidado;
import com.eventoapp.eventoapp.model.Evento;
import com.eventoapp.eventoapp.repository.ConvidadoRepository;
import com.eventoapp.eventoapp.repository.EventoRepository;

@Controller
public class EventoController {

	@Autowired
	private EventoRepository er;
	
	@Autowired
	private ConvidadoRepository cr;

	@RequestMapping(value = "/cadastrarEvento", method = RequestMethod.GET)
	public String from() {
		return "evento/formEvento";
	}

	@RequestMapping(value = "/cadastrarEvento", method = RequestMethod.POST)
	public String from(@Valid Evento evento,BindingResult result, RedirectAttributes attributes) {
		if(result.hasErrors()) {
			attributes.addFlashAttribute("mensagem", "verifique os campos");
			return "redirect:/cadastrarEvento";
		}
		
		attributes.addFlashAttribute("mensagem", "Convidado adicionado");
		er.save(evento);
		return "redirect:/cadastrarEvento";
	}

	@RequestMapping("/eventos")
	public ModelAndView listaEventos() {
		ModelAndView mv = new ModelAndView("index");
		Iterable<Evento> eventos = er.findAll();
		mv.addObject("eventos", eventos);

		return mv;
	}
	
	@RequestMapping(value="/{codigo}",method = RequestMethod.GET)
	public ModelAndView detalhesEvento(@PathVariable("codigo") long codigo) {
		Evento evento = er.findBycodigo(codigo);
		ModelAndView mv = new ModelAndView("evento/detalhesEvento");
		mv.addObject("evento", evento);
		
		Iterable<Convidado> convidados = cr.findByEvento(evento);
		mv.addObject("convidados",convidados);
		
		return mv;
	}
	
	@RequestMapping("/deletarEvento")
	public String deletarEvento(long codigo) {
		Evento evento = er.findBycodigo(codigo);
		Iterable<Convidado> convidados = cr.findByEvento(evento);
		cr.deleteAll(convidados);
		er.delete(evento);
		return "redirect:/Eventos";
	}
	
	
	
	@RequestMapping(value="/{codigo}",method = RequestMethod.POST)
	public String detalhesEvento(@PathVariable("codigo") long codigo,@Valid Convidado convidado, BindingResult result, RedirectAttributes attributes) {
		if(result.hasErrors()) {
			attributes.addFlashAttribute("mensagem", "verifique os campos");
			return "redirect:/{codigo}";
		}
		Evento evento = er.findBycodigo(codigo);
		convidado.setEvento(evento);
		cr.save(convidado);
		attributes.addFlashAttribute("mensagem", "Convidado adicionado");
		return "redirect:/{codigo}";
	}
	
	@RequestMapping("/deletarConvidado")
	public String deletarConvidado(String rg) {
		Convidado convidado = cr.findByRg(rg);
		cr.delete(convidado);
		
		Evento evento = convidado.getEvento();
		long codigoEvento = evento.getCodigo();
		
		return "redirect:/"+codigoEvento;
	}
	
}
