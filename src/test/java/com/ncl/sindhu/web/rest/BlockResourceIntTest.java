package com.ncl.sindhu.web.rest;

import com.ncl.sindhu.NclsindhuApp;

import com.ncl.sindhu.domain.Block;
import com.ncl.sindhu.repository.BlockRepository;
import com.ncl.sindhu.repository.search.BlockSearchRepository;
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
 * Test class for the BlockResource REST controller.
 *
 * @see BlockResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NclsindhuApp.class)
public class BlockResourceIntTest {

    private static final String DEFAULT_BLOCKID = "AAAAAAAAAA";
    private static final String UPDATED_BLOCKID = "BBBBBBBBBB";

    private static final String DEFAULT_BLOCKDESC = "AAAAAAAAAA";
    private static final String UPDATED_BLOCKDESC = "BBBBBBBBBB";

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private BlockSearchRepository blockSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restBlockMockMvc;

    private Block block;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BlockResource blockResource = new BlockResource(blockRepository, blockSearchRepository);
        this.restBlockMockMvc = MockMvcBuilders.standaloneSetup(blockResource)
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
    public static Block createEntity(EntityManager em) {
        Block block = new Block()
            .blockid(DEFAULT_BLOCKID)
            .blockdesc(DEFAULT_BLOCKDESC);
        return block;
    }

    @Before
    public void initTest() {
        blockSearchRepository.deleteAll();
        block = createEntity(em);
    }

    @Test
    @Transactional
    public void createBlock() throws Exception {
        int databaseSizeBeforeCreate = blockRepository.findAll().size();

        // Create the Block
        restBlockMockMvc.perform(post("/api/blocks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(block)))
            .andExpect(status().isCreated());

        // Validate the Block in the database
        List<Block> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeCreate + 1);
        Block testBlock = blockList.get(blockList.size() - 1);
        assertThat(testBlock.getBlockid()).isEqualTo(DEFAULT_BLOCKID);
        assertThat(testBlock.getBlockdesc()).isEqualTo(DEFAULT_BLOCKDESC);

        // Validate the Block in Elasticsearch
        Block blockEs = blockSearchRepository.findOne(testBlock.getId());
        assertThat(blockEs).isEqualToIgnoringGivenFields(testBlock);
    }

    @Test
    @Transactional
    public void createBlockWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = blockRepository.findAll().size();

        // Create the Block with an existing ID
        block.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBlockMockMvc.perform(post("/api/blocks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(block)))
            .andExpect(status().isBadRequest());

        // Validate the Block in the database
        List<Block> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkBlockidIsRequired() throws Exception {
        int databaseSizeBeforeTest = blockRepository.findAll().size();
        // set the field null
        block.setBlockid(null);

        // Create the Block, which fails.

        restBlockMockMvc.perform(post("/api/blocks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(block)))
            .andExpect(status().isBadRequest());

        List<Block> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBlocks() throws Exception {
        // Initialize the database
        blockRepository.saveAndFlush(block);

        // Get all the blockList
        restBlockMockMvc.perform(get("/api/blocks?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(block.getId().intValue())))
            .andExpect(jsonPath("$.[*].blockid").value(hasItem(DEFAULT_BLOCKID.toString())))
            .andExpect(jsonPath("$.[*].blockdesc").value(hasItem(DEFAULT_BLOCKDESC.toString())));
    }

    @Test
    @Transactional
    public void getBlock() throws Exception {
        // Initialize the database
        blockRepository.saveAndFlush(block);

        // Get the block
        restBlockMockMvc.perform(get("/api/blocks/{id}", block.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(block.getId().intValue()))
            .andExpect(jsonPath("$.blockid").value(DEFAULT_BLOCKID.toString()))
            .andExpect(jsonPath("$.blockdesc").value(DEFAULT_BLOCKDESC.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBlock() throws Exception {
        // Get the block
        restBlockMockMvc.perform(get("/api/blocks/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBlock() throws Exception {
        // Initialize the database
        blockRepository.saveAndFlush(block);
        blockSearchRepository.save(block);
        int databaseSizeBeforeUpdate = blockRepository.findAll().size();

        // Update the block
        Block updatedBlock = blockRepository.findOne(block.getId());
        // Disconnect from session so that the updates on updatedBlock are not directly saved in db
        em.detach(updatedBlock);
        updatedBlock
            .blockid(UPDATED_BLOCKID)
            .blockdesc(UPDATED_BLOCKDESC);

        restBlockMockMvc.perform(put("/api/blocks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBlock)))
            .andExpect(status().isOk());

        // Validate the Block in the database
        List<Block> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeUpdate);
        Block testBlock = blockList.get(blockList.size() - 1);
        assertThat(testBlock.getBlockid()).isEqualTo(UPDATED_BLOCKID);
        assertThat(testBlock.getBlockdesc()).isEqualTo(UPDATED_BLOCKDESC);

        // Validate the Block in Elasticsearch
        Block blockEs = blockSearchRepository.findOne(testBlock.getId());
        assertThat(blockEs).isEqualToIgnoringGivenFields(testBlock);
    }

    @Test
    @Transactional
    public void updateNonExistingBlock() throws Exception {
        int databaseSizeBeforeUpdate = blockRepository.findAll().size();

        // Create the Block

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restBlockMockMvc.perform(put("/api/blocks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(block)))
            .andExpect(status().isCreated());

        // Validate the Block in the database
        List<Block> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteBlock() throws Exception {
        // Initialize the database
        blockRepository.saveAndFlush(block);
        blockSearchRepository.save(block);
        int databaseSizeBeforeDelete = blockRepository.findAll().size();

        // Get the block
        restBlockMockMvc.perform(delete("/api/blocks/{id}", block.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean blockExistsInEs = blockSearchRepository.exists(block.getId());
        assertThat(blockExistsInEs).isFalse();

        // Validate the database is empty
        List<Block> blockList = blockRepository.findAll();
        assertThat(blockList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchBlock() throws Exception {
        // Initialize the database
        blockRepository.saveAndFlush(block);
        blockSearchRepository.save(block);

        // Search the block
        restBlockMockMvc.perform(get("/api/_search/blocks?query=id:" + block.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(block.getId().intValue())))
            .andExpect(jsonPath("$.[*].blockid").value(hasItem(DEFAULT_BLOCKID.toString())))
            .andExpect(jsonPath("$.[*].blockdesc").value(hasItem(DEFAULT_BLOCKDESC.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Block.class);
        Block block1 = new Block();
        block1.setId(1L);
        Block block2 = new Block();
        block2.setId(block1.getId());
        assertThat(block1).isEqualTo(block2);
        block2.setId(2L);
        assertThat(block1).isNotEqualTo(block2);
        block1.setId(null);
        assertThat(block1).isNotEqualTo(block2);
    }
}
