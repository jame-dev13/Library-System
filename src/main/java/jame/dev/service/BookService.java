package jame.dev.service;

import jame.dev.models.entitys.BookEntity;
import jame.dev.models.enums.EGenre;
import jame.dev.repositorys.CRUDRepo;
import jame.dev.utils.db.DMLActions;
import jame.dev.utils.db.DQLActions;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * Provides implementations to perform simple CRUD actions on DB and return
 * his object representation.
 */
public final class BookService implements CRUDRepo<BookEntity> {
    @Override
    public List<BookEntity> getAll() {
        String sql = """
                SELECT * FROM books;
                """;
        return DQLActions.select(sql,
                rs-> BookEntity
                        .builder()
                        .id(rs.getInt(1))
                        .uuid(UUID.fromString(rs.getString(2)))
                        .title(rs.getString(3))
                        .author(rs.getString(4))
                        .editorial(rs.getString(5))
                        .ISBN(rs.getString(6))
                        .pubDate(rs.getDate(7).toLocalDate())
                        .numPages(rs.getInt(8))
                        .genre(EGenre.valueOf(rs.getString(9)))
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
                bookEntity.getGenre().name(),
        };
        String sql = """
                INSERT INTO books
                (uuid, title, author, editorial, ISBN, publication_date, pages, genre)
                VALUES (?,?,?,?,?,?,?,?);
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
                .id(rs.getInt(1))
                .uuid(UUID.fromString(rs.getString(2)))
                .title(rs.getString(3))
                .author(rs.getString(4))
                .editorial(rs.getString(5))
                .ISBN(rs.getString(6))
                .pubDate(rs.getDate(7).toLocalDate())
                .numPages(rs.getInt(8))
                .genre(EGenre.valueOf(rs.getString(9)))
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
                pages = ?, genre = ?
                WHERE uuid = ?
                """;
        Object[] params = {
                book.getTitle(), book.getAuthor(),
                book.getEditorial(), book.getISBN(),
                book.getPubDate(), book.getNumPages(),
                book.getGenre().name(),
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
