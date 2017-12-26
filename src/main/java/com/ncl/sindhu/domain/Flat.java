package com.ncl.sindhu.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Flat.
 */
@Entity
@Table(name = "flat")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "flat")
public class Flat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "flatid", nullable = false)
    private String flatid;

    @Column(name = "flatdesc")
    private String flatdesc;

    @ManyToOne
    private Block block;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFlatid() {
        return flatid;
    }

    public Flat flatid(String flatid) {
        this.flatid = flatid;
        return this;
    }

    public void setFlatid(String flatid) {
        this.flatid = flatid;
    }

    public String getFlatdesc() {
        return flatdesc;
    }

    public Flat flatdesc(String flatdesc) {
        this.flatdesc = flatdesc;
        return this;
    }

    public void setFlatdesc(String flatdesc) {
        this.flatdesc = flatdesc;
    }

    public Block getBlock() {
        return block;
    }

    public Flat block(Block block) {
        this.block = block;
        return this;
    }

    public void setBlock(Block block) {
        this.block = block;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Flat flat = (Flat) o;
        if (flat.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), flat.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Flat{" +
            "id=" + getId() +
            ", flatid='" + getFlatid() + "'" +
            ", flatdesc='" + getFlatdesc() + "'" +
            "}";
    }
}
