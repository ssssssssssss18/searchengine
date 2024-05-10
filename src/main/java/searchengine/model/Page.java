package searchengine.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Page extends AbstractEntity {

    private String path;

    @Column(name = "status_code")
    private int statusCode;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;

    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL)
    private List<Index> index;

    public Page(String path, int statusCode, String content, Site site) {
        this.path = path;
        this.statusCode = statusCode;
        this.content = content;
        this.site = site;
    }
}
