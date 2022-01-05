package com.project.app.controller;

import com.project.app.dto.EbookAuthorDTO;
import com.project.app.dto.EbookDTO;
import com.project.app.dto.ebook.Item;
import com.project.app.dto.ebook.Root;
import com.project.app.entity.Ebook;
import com.project.app.entity.EbookAuthor;
import com.project.app.request.EbookAPI;
import com.project.app.response.PageResponse;
import com.project.app.response.Response;
import com.project.app.service.EbookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Properties;

@RestController
@RequestMapping("/api/v5/ebooks")
public class EbookController {

    private final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";
    private Properties properties = new Properties();
    private String apikey = properties.getProperty("apikey");

    @Autowired
    EbookService ebookService;

    @Autowired
    RestTemplate restTemplate;

    @PostMapping
    public ResponseEntity<Response<Ebook>> saveEbook(
            @RequestBody EbookAPI ebookAPI
    ) {
        Response<Ebook> response = new Response<>(
                "Success: Data saved successfully!",
                ebookService.saveEbookToDB(ebookAPI)
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<Response<PageResponse<Ebook>>> getSavedEbooks(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "5") Integer size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Ebook> ebooks = ebookService.getSavedEbooks(pageable);

        PageResponse<Ebook> response = new PageResponse<>(
                ebooks.getContent(),
                ebooks.getTotalElements(),
                ebooks.getTotalPages(),
                page,
                size
        );

        return ResponseEntity.ok(
                new Response<>(
                        "Success: data saved successfully!",
                        response
                )
        );
    }

    @GetMapping("/title")
    public ResponseEntity<Response<PageResponse<Ebook>>> getSavedEbookByTitle(
            @RequestParam(name = "q") String title,
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "5") Integer size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        EbookDTO ebookDTO = new EbookDTO(title);
        Page<Ebook> ebook = ebookService.getSavedEbookByTitle(ebookDTO, pageable);
        PageResponse<Ebook> response = new PageResponse<>(
                ebook.getContent(),
                ebook.getTotalElements(),
                ebook.getTotalPages(),
                page,
                size
        );
        return ResponseEntity.ok(new Response<>(
                "Success: get data successfully!", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> deleteSavedEbookById(
            @PathVariable(name = "id") String id
    ) {
        ebookService.deleteSavedEbook(id);

        Response<String> response = new Response<>("Success: deleted successfully!", id);
        return ResponseEntity.ok(response);
    }


    //API
    @GetMapping(
            value = "/{code}",
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<Response<EbookAPI>> getEbookAPIByCode(
            @PathVariable(name = "code") String code
    ) {
        String url = BASE_URL + "/" + code + "?apiKey=" + apikey;

        //Json to JavaObject
        Item ebooks = restTemplate.getForObject(url, Item.class);
        Response<EbookAPI> response = new Response<>(
                "Succeed: requested ebook by id " + code + " successfully!",
                ebookService.mapEbookAPIByCode(ebooks)
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping(
            value = "/author",
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public Response<List<EbookAPI>> getEbookAPIByAuthor(
            @RequestParam(value = "q", defaultValue = "") String query,
            @RequestParam(value = "author", defaultValue = "") String author
    ) {
        String url = BASE_URL + "?q=" + query + "+inauthor:"+ author +"&filter=free-ebooks&apiKey=" + apikey;

        //Json to JavaObject
        Root ebooks = restTemplate.getForObject(url, Root.class);
        return new Response<List<EbookAPI>>("Success: get api successfully!", ebookService.mapListEbookAPI(ebooks));
    }

    @GetMapping(
            value = "/search",
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public Response<List<EbookAPI>> getEbooksFromAPI(
            @RequestParam("q") String query
    ) {
        String url = BASE_URL + "?q=" + query + "&filter=free-ebooks&apiKey=" + apikey;

        //Json to JavaObject
        Root ebooks = restTemplate.getForObject(url, Root.class);
        return new Response<List<EbookAPI>>("Success: get api successfully!", ebookService.mapListEbookAPI(ebooks));
    }

}
