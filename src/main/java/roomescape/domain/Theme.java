package roomescape.domain;

import java.util.Objects;
import roomescape.exception.CustomInvalidDomainException;
import roomescape.exception.ErrorCode;

public class Theme {

    private final String name;
    private final String description;
    private final String thumbnailUrl;

    public Theme(String name, String description, String thumbnailUrl) {
        validate(name, description, thumbnailUrl);
        this.name = name;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
    }

    private void validate(String name, String description, String thumbnailUrl) {
        if (name == null || name.isBlank()) {
            throw new CustomInvalidDomainException(ErrorCode.NOT_ALLOW_NAME_NULL);
        }
        if (description == null || description.isBlank()) {
            throw new CustomInvalidDomainException(ErrorCode.NOT_ALLOW_DESCRIPTION_NULL);
        }
        if (thumbnailUrl == null || thumbnailUrl.isBlank()) {
            throw new CustomInvalidDomainException(ErrorCode.NOT_ALLOW_THUMBNAIL_NULL);
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Theme theme = (Theme) object;
        return Objects.equals(name, theme.name) && Objects.equals(description, theme.description)
                && Objects.equals(thumbnailUrl, theme.thumbnailUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, thumbnailUrl);
    }
}
