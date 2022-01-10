package com.project.app.service.impl;

import com.project.app.entity.*;
import com.project.app.repository.ReturnBookRepository;
import com.project.app.service.LoanService;
import com.project.app.service.ReturnBookDetailService;
import com.project.app.service.ReturnBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReturnBookServiceImpl implements ReturnBookService {
    @Autowired
    private ReturnBookRepository repository;

    @Autowired
    private ReturnBookDetailService returnBookDetailService;

    @Autowired
    private LoanService loanService;

    @Override
    @Transactional
    public ReturnBook createTransaction(ReturnBook returnBook) {
        Integer totalQty = 0;
        Integer totalPenaltyFee = 0;
        Integer overdueDuration = 0;
        Integer overdueFee = returnBook.getOverdueFee();

        Loan loan = loanService.getById(returnBook.getLoan().getId());

        // validasi, if book is returned then reject this request.
        if (loan.getReturnStatus()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"cannot return book that has been returned");
        }

        for (int i = 0; i < loan.getLoanDetail().size(); i++) {
            ReturnBookDetail returnBookDetail = returnBook.getReturnBookDetails().get(i);
            LoanDetail loanDetail = loan.getLoanDetail().get(i);

            // tambahkan validasi, jika return book detail != loan book detail
            if (!returnBookDetail.getBook().getId().equals(loanDetail.getBook().getId())) {
                String message = "fill your return book request as loan book";
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,message);
            }

            // Tambahin Validasi, jumlah pengembalian tidak lebih dari jumlah peminjaman.
            Integer lostBook = loanDetail.getQty() - returnBookDetail.getQty();
            if (lostBook < 0){
                String message = String.format("book loaned : %d, book returned : %d", loanDetail.getQty(), returnBookDetail.getQty());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,message);
            }

            // Tambahkan Stock buku perpus sesuai jumlah pengembalian;
            Book book = loanDetail.getBook();
            book.setStock(returnBookDetail.getQty());
//            bookService.update(book); // tambahin nanti saat merging.

            // TAMBAHIN - logic jumlah hari keterlambatan;
            LocalDateTime dateDue = loan.getDateDue();
            LocalDateTime dateReturn = LocalDateTime.now();
            Duration duration = Duration.between(dateDue, dateReturn);
            if (duration.toDays() < 1 ){
                overdueDuration = 0;
            } else {
                overdueDuration = Math.toIntExact(duration.toDays());
            }

            // bookPrice nanti ganti sesuai harga buku saat merging.
            // isi info returnBookDetail ( isi penalty fee ).
            Integer bookPrice = 50000;
            Integer penaltyFee = overdueFee * overdueDuration + bookPrice * lostBook;
            returnBookDetail.setPenaltyFee(penaltyFee);
            returnBookDetailService.create(returnBookDetail);

            totalPenaltyFee += penaltyFee;
            totalQty += returnBookDetail.getQty();
        }
        // isi info Loan
        loan.setReturnStatus(true);
        loanService.update(loan);

        // Isi totalQty dan Totalpenalty ke Return
        returnBook.setLoan(loan);
        returnBook.setTotalQty(totalQty);
        returnBook.setTotalPenaltyFee(totalPenaltyFee);

        return repository.save(returnBook);
    }

    @Override
    public List<ReturnBook> getReturnBooks() {
        return repository.findAll();
    }

    @Override
    public ReturnBook loadReturnBookByLoan(Loan loan) {
        Optional<ReturnBook> returnBook = repository.findByLoan(loan);
        if (returnBook.isPresent()){
            return returnBook.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("there is no return info with loan id %s ", loan.getId()));
    }
}
