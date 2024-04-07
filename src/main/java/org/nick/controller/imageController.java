package org.nick.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/s3")
public class imageController {

    private ImageUploader uploader;

    public imageController(ImageUploader uploader){
        this.uploader = uploader;
    }

    //Upload Image
    @PostMapping(path = "/upload")
    public ResponseEntity<String> uploadImage(@RequestParam MultipartFile file){
        return ResponseEntity.ok(uploader.uploadImage(file));
    }

    //Get All Images
    @GetMapping
    public ResponseEntity<List<String>> getAllFiles(){
        return ResponseEntity.ok(uploader.allFiles());

    }

    @GetMapping("/{fileName}")
    public ResponseEntity<String> getUrlByFileName(@PathVariable("fileName") String fileName) {
        return ResponseEntity.ok(uploader.getImageUrlByName(fileName));
    }
}
