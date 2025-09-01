package jame.dev.service;

import jame.dev.models.entitys.BookEntity;
import jame.dev.models.enums.ECategory;
import jame.dev.models.enums.ELanguage;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.utils.DMLActions;
import jame.dev.utils.DQLActions;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


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
                        .uuid(UUID.fromString(rs.getString("uuid")))
                        .title(rs.getString("title"))
                        .author(rs.getString("author"))
                        .editorial(rs.getString("editorial"))
                        .category(ECategory.valueOf(rs.getString("category")))
                        .ISBN(rs.getString("ISBN"))
                        .pubDate(rs.getDate("publication_date").toLocalDate())
                        .numPages(rs.getInt("pages"))
                        .genre(rs.getString("genre"))
                        .language(ELanguage.valueOf(rs.getString("language")))
                        .build()
        );
    }

    @Override
    public void save(BookEntity bookEntity)  {
        Object[] params = {
                bookEntity.getUuid().toString(),
                bookEntity.getTitle(), bookEntity.getAuthor(),
                bookEntity.getEditorial(), bookEntity.getISBN(),
                bookEntity.getPubDate(), bookEntity.getNumPages(),
                bookEntity.getGenre(), bookEntity.getLanguage().name(),
                bookEntity.getCategory().name(),
        };
        String sql = """
                INSERT INTO books
                (uuid, title, author, editorial, ISBN, publication_date, pages, genre, language, category)
                VALUES (?,?,?,?,?,?,?,?,?,?);
                """;
        try{
            DMLActions.insert(sql, params);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<BookEntity> findByUuid(UUID uuid) {
        String sql = """
                SELECT * FROM books WHERE uuid = ?
                """;
        List<BookEntity> books = DQLActions.selectWhere(sql, rs-> BookEntity
                .builder()
                .uuid(UUID.fromString(rs.getString("uuid")))
                .title(rs.getString("title"))
                .author(rs.getString("author"))
                .editorial(rs.getString("editorial"))
                .category(ECategory.valueOf("category"))
                .ISBN(rs.getString("ISBN"))
                .pubDate(rs.getDate("publication_date").toLocalDate())
                .numPages(rs.getInt("pages"))
                .genre(rs.getString("genre"))
                .language(ELanguage.valueOf(rs.getString("language")))
                .build(),
                uuid.toString());
        return Optional.of(books.getFirst());
    }

    @Override
    public void update(BookEntity book) {
        String sql = """
                UPDATE books SET
                title = ?, author = ?,
                editorial = ?, ISBN = ?,
                publication_date = ?,
                pages = ?, genre = ?,
                language = ?, category = ? WHERE uuid = ?
                """;
        Object[] params = {
                book.getTitle(), book.getAuthor(),
                book.getEditorial(), book.getISBN(),
                book.getPubDate(), book.getNumPages(),
                book.getGenre(), book.getLanguage().name(), book.getCategory().name(),
                book.getUuid().toString()
        };
        try{
            DMLActions.update(sql, params);
        }catch (SQLException e ){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteByUuid(UUID uuid) {
        String sql = """
                DELETE FROM books WHERE uuid = ?
                """;
        try {
            DMLActions.delete(sql, uuid.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
