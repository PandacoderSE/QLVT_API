package DATN.ITDeviceManagement.DTO.request;


import javax.validation.constraints.NotNull;

public class CategoryRequest {
    private Long id;
    @NotNull(message = "Tên danh mục là trường bắt buôc")
    private String name;
    private String description;

    public CategoryRequest() {
    }

    public CategoryRequest(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
