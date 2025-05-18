package DATN.ITDeviceManagement.DTO.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class PageResponse<T>{
    private List<T> contents;
    private Long offset;
    private int pageSize;
    private int page;
    private Boolean last;
    private Long totalElement;
    private int totalPages;
    private Boolean first;
    private int numberOfElement;
    private Boolean isEmpty;

}
