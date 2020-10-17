package com.example.javardmvc.controller;

import static java.util.Objects.nonNull;
import static java.util.UUID.randomUUID;

import com.example.javardmvc.domain.Message;
import com.example.javardmvc.domain.User;
import com.example.javardmvc.repository.MessageRepo;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class MainController {

  @Autowired
  private MessageRepo messageRepo;

  @Value("${upload.path}")
  private String uploadPath;

  @GetMapping("/")
  public String greeting(Map<String, Object> model) {
    return "greeting";
  }

  @GetMapping("/main")
  public String main(@RequestParam(required = false, defaultValue = "") String filter,
      Model model) {
    Iterable<Message> messages = messageRepo.findAll();

    if (filter != null && !filter.isEmpty()) {
      messages = messageRepo.findByTag(filter);
    } else {
      messages = messageRepo.findAll();
    }

    model.addAttribute("messages", messages);
    model.addAttribute("filter", filter);

    return "main";
  }

  @PostMapping("/main")
  public String add(
      @AuthenticationPrincipal User user,
      @RequestParam String text,
      @RequestParam String tag, Map<String, Object> model,
      @RequestParam("file") MultipartFile file) throws IOException {
    Message message = new Message(text, tag, user);

    if (nonNull(file) && !file.getOriginalFilename().isEmpty()) {
      File uploadDir = new File(uploadPath);
      if (!uploadDir.exists()) {
        uploadDir.mkdir();
      }

      String uuidFile = randomUUID().toString();
      String resultFilename = uuidFile + "." + file.getOriginalFilename();

      file.transferTo(new File(uploadPath + "/" + resultFilename));

      message.setFilename(resultFilename);
    }

    messageRepo.save(message);

    Iterable<Message> messages = messageRepo.findAll();

    model.put("messages", messages);

    return "main";
  }
}
