package searchengine.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "word_index")
public class Index extends AbstractEntity {
    @ManyToOne
    @JoinColumn(name = "page_id")
    private Page page;
    @ManyToOne
    @JoinColumn(name = "lemma_id")
    private Lemma lemma;
    @Column(name = "word_rank")
    private Float wordRank;

    public Index(Page page, Lemma lemma, Float wordRank) {
        this.page = page;
        this.lemma = lemma;
        this.wordRank = wordRank;
    }
}
