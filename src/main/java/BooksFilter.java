import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class BooksFilter {

    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            File booksFile = new File("books.json");

            Book[] books = objectMapper.readValue(booksFile, Book[].class);

            List<Book> bookList = Arrays.asList(books);

            filter("exampleText", bookList);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Optional<BookDate> filter(String filter, List<Book> books) {
        List<Book> booksWithoutPublishDate = books.stream().filter(book -> Objects.isNull(book.getPublicationTimestamp())).collect(Collectors.toList());
        System.out.println("Libros sin fecha de pulblicación: ");
        booksWithoutPublishDate.forEach(System.out::println);

        books = books.stream()
                .filter(book -> (Objects.nonNull(book.getTitle()) && book.getTitle().toLowerCase().contains(filter.toLowerCase())) &&
                        (Objects.nonNull(book.getSummary()) && book.getSummary().toLowerCase().contains(filter.toLowerCase())) &&
                        (Objects.nonNull(book.getAuthor()) && Objects.nonNull(book.getAuthor().getBio()) && book.getAuthor().getBio().toLowerCase().contains(filter.toLowerCase())))
                .collect(Collectors.toList());

        if (books.size() > 1) {

            books = books.stream()
                    .filter(book -> Objects.nonNull(book.getPublicationTimestamp()))
                    .sorted(Comparator.comparing(Book::getPublicationTimestamp))
                    .collect(Collectors.toList());

            if (!books.isEmpty()) {
                return createBookDate(books.getFirst());
            }


        } else if (books.size() == 1) {

            return createBookDate(books.getFirst());
        }

        return Optional.empty();
    }

    private static Optional<BookDate> createBookDate(Book book) {
        BookDate bookDate = new BookDate();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm-dd-yyyy");
        String formatedDate = currentDate.format(formatter);

        bookDate.setId(book.getId());
        bookDate.setTitle(book.getTitle());
        bookDate.setPages(book.getPages());
        bookDate.setPublicationTimestamp(book.getPublicationTimestamp());
        bookDate.setSummary(book.getSummary());
        bookDate.setAuthor(book.getAuthor());

        bookDate.setDate(formatedDate);

        return Optional.of(bookDate);
    }


}
