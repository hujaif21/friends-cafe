package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ReservationStatusController {
	@PostMapping("/resStatus")
	public String resStatus(Model model)
	{
		return "redirect:/reserve-status";
	}
}
