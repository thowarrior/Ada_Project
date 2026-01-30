package ada.project.infrastructure.entity;

import java.util.List;
//! Parece com uma classe, é usada para determinar um tipo imutável
public record PageResult<T> (
        List<T> items,
        long totalItems,
        int totalPages,
        int page,
        int pageSize
){
}
