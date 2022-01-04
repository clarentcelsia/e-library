package com.project.app.service;

import com.project.app.entity.ProductionBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;


public interface ProductionBookService {

    ProductionBook save(ProductionBook productionBook, MultipartFile...multipartFiles);

    Page<ProductionBook> getAll(Pageable pageable);

    ProductionBook getById(String id);

    ProductionBook updateWithMultipart(ProductionBook productionBook, MultipartFile...multipartFiles);

    ProductionBook update(ProductionBook productionBook);

    void deleteById(String id);
}
