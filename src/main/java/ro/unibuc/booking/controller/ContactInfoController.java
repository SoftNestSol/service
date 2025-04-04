package ro.unibuc.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ro.unibuc.booking.exception.EntityNotFoundException;
import ro.unibuc.booking.dto.ContactInfoDTO;
import ro.unibuc.booking.service.ContactService;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class ContactInfoController {

    @Autowired
    private ContactService contactService;

    @GetMapping("/contact-info")
    @ResponseBody
    public List<ContactInfoDTO> getContactInfo() {
        return contactService.getContactInfo();
    }

    // @PutMapping("/contact-info")
    // @ResponseBody
    // public ContactInfoDTO updateContactInfo(@RequestBody ContactInfoDTO contactInfoDTO) {
    //     return contactService.updateContactInfo(contactInfoDTO);
    // }
}