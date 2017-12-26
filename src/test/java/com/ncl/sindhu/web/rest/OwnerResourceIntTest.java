package com.ncl.sindhu.web.rest;

import com.ncl.sindhu.NclsindhuApp;

import com.ncl.sindhu.domain.Owner;
import com.ncl.sindhu.repository.OwnerRepository;
import com.ncl.sindhu.repository.search.OwnerSearchRepository;
import com.ncl.sindhu.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.ncl.sindhu.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the OwnerResource REST controller.
 *
 * @see OwnerResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NclsindhuApp.class)
public class OwnerResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_MOBILE = "AAAAAAAAAA";
    private static final String UPDATED_MOBILE = "BBBBBBBBBB";

    private static final String DEFAULT_CARLOCATION = "AAAAAAAAAA";
    private static final String UPDATED_CARLOCATION = "BBBBBBBBBB";

    private static final String DEFAULT_INTERCOM = "AAAAAAAAAA";
    private static final String UPDATED_INTERCOM = "BBBBBBBBBB";

    private static final String DEFAULT_CARNUMBER = "AAAAAAAAAA";
    private static final String UPDATED_CARNUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_BIKENUMBER = "AAAAAAAAAA";
    private static final String UPDATED_BIKENUMBER = "BBBBBBBBBB";

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private OwnerSearchRepository ownerSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restOwnerMockMvc;

    private Owner owner;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final OwnerResource ownerResource = new OwnerResource(ownerRepository, ownerSearchRepository);
        this.restOwnerMockMvc = MockMvcBuilders.standaloneSetup(ownerResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Owner createEntity(EntityManager em) {
        Owner owner = new Owner()
            .name(DEFAULT_NAME)
            .mobile(DEFAULT_MOBILE)
            .carlocation(DEFAULT_CARLOCATION)
            .intercom(DEFAULT_INTERCOM)
            .carnumber(DEFAULT_CARNUMBER)
            .bikenumber(DEFAULT_BIKENUMBER);
        return owner;
    }

    @Before
    public void initTest() {
        ownerSearchRepository.deleteAll();
        owner = createEntity(em);
    }

    @Test
    @Transactional
    public void createOwner() throws Exception {
        int databaseSizeBeforeCreate = ownerRepository.findAll().size();

        // Create the Owner
        restOwnerMockMvc.perform(post("/api/owners")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(owner)))
            .andExpect(status().isCreated());

        // Validate the Owner in the database
        List<Owner> ownerList = ownerRepository.findAll();
        assertThat(ownerList).hasSize(databaseSizeBeforeCreate + 1);
        Owner testOwner = ownerList.get(ownerList.size() - 1);
        assertThat(testOwner.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testOwner.getMobile()).isEqualTo(DEFAULT_MOBILE);
        assertThat(testOwner.getCarlocation()).isEqualTo(DEFAULT_CARLOCATION);
        assertThat(testOwner.getIntercom()).isEqualTo(DEFAULT_INTERCOM);
        assertThat(testOwner.getCarnumber()).isEqualTo(DEFAULT_CARNUMBER);
        assertThat(testOwner.getBikenumber()).isEqualTo(DEFAULT_BIKENUMBER);

        // Validate the Owner in Elasticsearch
        Owner ownerEs = ownerSearchRepository.findOne(testOwner.getId());
        assertThat(ownerEs).isEqualToIgnoringGivenFields(testOwner);
    }

    @Test
    @Transactional
    public void createOwnerWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = ownerRepository.findAll().size();

        // Create the Owner with an existing ID
        owner.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restOwnerMockMvc.perform(post("/api/owners")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(owner)))
            .andExpect(status().isBadRequest());

        // Validate the Owner in the database
        List<Owner> ownerList = ownerRepository.findAll();
        assertThat(ownerList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = ownerRepository.findAll().size();
        // set the field null
        owner.setName(null);

        // Create the Owner, which fails.

        restOwnerMockMvc.perform(post("/api/owners")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(owner)))
            .andExpect(status().isBadRequest());

        List<Owner> ownerList = ownerRepository.findAll();
        assertThat(ownerList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOwners() throws Exception {
        // Initialize the database
        ownerRepository.saveAndFlush(owner);

        // Get all the ownerList
        restOwnerMockMvc.perform(get("/api/owners?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(owner.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].mobile").value(hasItem(DEFAULT_MOBILE.toString())))
            .andExpect(jsonPath("$.[*].carlocation").value(hasItem(DEFAULT_CARLOCATION.toString())))
            .andExpect(jsonPath("$.[*].intercom").value(hasItem(DEFAULT_INTERCOM.toString())))
            .andExpect(jsonPath("$.[*].carnumber").value(hasItem(DEFAULT_CARNUMBER.toString())))
            .andExpect(jsonPath("$.[*].bikenumber").value(hasItem(DEFAULT_BIKENUMBER.toString())));
    }

    @Test
    @Transactional
    public void getOwner() throws Exception {
        // Initialize the database
        ownerRepository.saveAndFlush(owner);

        // Get the owner
        restOwnerMockMvc.perform(get("/api/owners/{id}", owner.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(owner.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.mobile").value(DEFAULT_MOBILE.toString()))
            .andExpect(jsonPath("$.carlocation").value(DEFAULT_CARLOCATION.toString()))
            .andExpect(jsonPath("$.intercom").value(DEFAULT_INTERCOM.toString()))
            .andExpect(jsonPath("$.carnumber").value(DEFAULT_CARNUMBER.toString()))
            .andExpect(jsonPath("$.bikenumber").value(DEFAULT_BIKENUMBER.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingOwner() throws Exception {
        // Get the owner
        restOwnerMockMvc.perform(get("/api/owners/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOwner() throws Exception {
        // Initialize the database
        ownerRepository.saveAndFlush(owner);
        ownerSearchRepository.save(owner);
        int databaseSizeBeforeUpdate = ownerRepository.findAll().size();

        // Update the owner
        Owner updatedOwner = ownerRepository.findOne(owner.getId());
        // Disconnect from session so that the updates on updatedOwner are not directly saved in db
        em.detach(updatedOwner);
        updatedOwner
            .name(UPDATED_NAME)
            .mobile(UPDATED_MOBILE)
            .carlocation(UPDATED_CARLOCATION)
            .intercom(UPDATED_INTERCOM)
            .carnumber(UPDATED_CARNUMBER)
            .bikenumber(UPDATED_BIKENUMBER);

        restOwnerMockMvc.perform(put("/api/owners")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedOwner)))
            .andExpect(status().isOk());

        // Validate the Owner in the database
        List<Owner> ownerList = ownerRepository.findAll();
        assertThat(ownerList).hasSize(databaseSizeBeforeUpdate);
        Owner testOwner = ownerList.get(ownerList.size() - 1);
        assertThat(testOwner.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testOwner.getMobile()).isEqualTo(UPDATED_MOBILE);
        assertThat(testOwner.getCarlocation()).isEqualTo(UPDATED_CARLOCATION);
        assertThat(testOwner.getIntercom()).isEqualTo(UPDATED_INTERCOM);
        assertThat(testOwner.getCarnumber()).isEqualTo(UPDATED_CARNUMBER);
        assertThat(testOwner.getBikenumber()).isEqualTo(UPDATED_BIKENUMBER);

        // Validate the Owner in Elasticsearch
        Owner ownerEs = ownerSearchRepository.findOne(testOwner.getId());
        assertThat(ownerEs).isEqualToIgnoringGivenFields(testOwner);
    }

    @Test
    @Transactional
    public void updateNonExistingOwner() throws Exception {
        int databaseSizeBeforeUpdate = ownerRepository.findAll().size();

        // Create the Owner

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restOwnerMockMvc.perform(put("/api/owners")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(owner)))
            .andExpect(status().isCreated());

        // Validate the Owner in the database
        List<Owner> ownerList = ownerRepository.findAll();
        assertThat(ownerList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteOwner() throws Exception {
        // Initialize the database
        ownerRepository.saveAndFlush(owner);
        ownerSearchRepository.save(owner);
        int databaseSizeBeforeDelete = ownerRepository.findAll().size();

        // Get the owner
        restOwnerMockMvc.perform(delete("/api/owners/{id}", owner.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean ownerExistsInEs = ownerSearchRepository.exists(owner.getId());
        assertThat(ownerExistsInEs).isFalse();

        // Validate the database is empty
        List<Owner> ownerList = ownerRepository.findAll();
        assertThat(ownerList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchOwner() throws Exception {
        // Initialize the database
        ownerRepository.saveAndFlush(owner);
        ownerSearchRepository.save(owner);

        // Search the owner
        restOwnerMockMvc.perform(get("/api/_search/owners?query=id:" + owner.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(owner.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].mobile").value(hasItem(DEFAULT_MOBILE.toString())))
            .andExpect(jsonPath("$.[*].carlocation").value(hasItem(DEFAULT_CARLOCATION.toString())))
            .andExpect(jsonPath("$.[*].intercom").value(hasItem(DEFAULT_INTERCOM.toString())))
            .andExpect(jsonPath("$.[*].carnumber").value(hasItem(DEFAULT_CARNUMBER.toString())))
            .andExpect(jsonPath("$.[*].bikenumber").value(hasItem(DEFAULT_BIKENUMBER.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Owner.class);
        Owner owner1 = new Owner();
        owner1.setId(1L);
        Owner owner2 = new Owner();
        owner2.setId(owner1.getId());
        assertThat(owner1).isEqualTo(owner2);
        owner2.setId(2L);
        assertThat(owner1).isNotEqualTo(owner2);
        owner1.setId(null);
        assertThat(owner1).isNotEqualTo(owner2);
    }
}
