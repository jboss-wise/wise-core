package org.jboss.wise.core.client.jaxrs;


import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/bookstore")
public class BookStore {

    private Map<Long, Book> books = new HashMap<Long, Book>();
    private long bookId = 123;
    
    private String currentBookId;

    public BookStore() {
        init();
    }

    @GET
    @Path("/books/{bookId}/")
    public Book getBook(@PathParam("bookId") String id) throws BookNotFoundFault {
        return doGetBook(id);
    }
    
    private Book doGetBook(String id) throws BookNotFoundFault {
        System.out.println("----invoking getBook with id: " + id);
        Book book = books.get(Long.parseLong(id));
        if (book != null) {
            return book;
        } else {
            BookNotFoundDetails details = new BookNotFoundDetails();
            details.setId(Long.parseLong(id));
            throw new BookNotFoundFault(details);
        }
    }

    @POST
    @Path("/books")
    @Produces("application/xml")
    @Consumes("application/xml")
    public Response addBook(Book book) {
        book.setId(++bookId);
        books.put(book.getId(), book);

        return Response.ok(book).build();
    }
    
    @PUT
    @Path("/books/")
    public Response updateBook(Book book) {
        Book b = books.get(book.getId());

        Response r;
        if (b != null) {
            books.put(book.getId(), book);
            r = Response.ok().build();
        } else {
            r = Response.notModified().build();
        }

        return r;
    }
    
    @DELETE
    @Path("/books/{bookId}/")
    public Response deleteBook(@PathParam("bookId") String id) {
        Book b = books.get(Long.parseLong(id));

        Response r;
        if (b != null) {
            r = Response.ok().build();
        } else {
            r = Response.notModified().build();
        }

        return r;
    }
    
    final void init() {
        Book book = new Book();
        book.setId(bookId);
        book.setName("CXF in Action");
        books.put(book.getId(), book);
    }
}


