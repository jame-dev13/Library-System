package jame.dev.service;

import jame.dev.models.entitys.BookEntity;
import jame.dev.models.enums.ELanguage;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.utils.DMLActions;
import jame.dev.utils.DQLActions;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;


/**
 * Provides implementations to perform simple CRUD actions on DB and return
 * his object representation.
 */
public class BookService implements CRUDRepo<BookEntity> {
    @Override
    public List<BookEntity> getAll() {
        String sql = """
                SELECT * FROM books;
                """;
        return DQLActions.select(sql,
                rs-> BookEntity
                        .builder()
                        .title(rs.getString("title"))
                        .author(rs.getString("author"))
                        .editorial(rs.getString("editorial"))
                        .ISBN(rs.getString("ISBN"))
                        .pubDate(rs.getDate("publication_date"))
                        .numPages(rs.getInt("pages"))
                        .genre(rs.getString("genre"))
                        .language(ELanguage.valueOf(rs.getString("language")))
                        .build()
        );
    }

    @Override
    public void save(BookEntity bookEntity)  {
        Object[] params = {
                bookEntity.getTitle(), bookEntity.getAuthor(),
                bookEntity.getEditorial(), bookEntity.getISBN(),
                bookEntity.getPubDate(), bookEntity.getNumPages(),
                bookEntity.getGenre(), bookEntity.getLanguage()
        };
        String sql = """
                INSERT INTO books
                (title, author, editorial, ISBN, publication_date, pages, genre, language)
                VALUES (?,?,?,?,?,?,?,?);
                """;
        try{
            DMLActions.insert(sql, params);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<BookEntity> findById(Integer id) {
        String sql = """
                SELECT * FROM books WHERE Id = ?
                """;
        List<BookEntity> books = DQLActions.selectWhere(sql, rs-> BookEntity
                .builder()
                .title(rs.getString("title"))
                .author(rs.getString("author"))
                .editorial(rs.getString("editorial"))
                .ISBN(rs.getString("ISBN"))
                .pubDate(rs.getDate("publication_date"))
                .numPages(rs.getInt("pages"))
                .genre(rs.getString("genre"))
                .language(ELanguage.valueOf(rs.getString("language")))
                .build(),
                id);
        return Optional.of(books.getFirst());
    }

    @Override
    public void updateById(BookEntity book) {
        String sql = """
                UPDATE books SET
                title = ?, author = ?,
                editorial = ?, ISBN = ?,
                publication_date = ?,
                pages = ?, genre = ?,
                language = ? WHERE id = ?
                """;
        Object[] params = {
                book.getTitle(), book.getAuthor(),
                book.getEditorial(), book.getISBN(),
                book.getPubDate(), book.getNumPages(),
                book.getGenre(), book.getLanguage(), book.getId()
        };
        try{
            DMLActions.update(sql, params);
        }catch (SQLException e ){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Integer id) {
        String sql = """
                DELETE FROM books WHERE id = ?
                """;
        try {
            DMLActions.delete(sql, id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
