package com.dreamland.service;

import com.dreamland.dto.PaginationDTO;
import com.dreamland.dto.QuestionDTO;
import com.dreamland.exception.CustomizeErrorCode;
import com.dreamland.exception.CustomizeException;
import com.dreamland.mapper.QuestionMapper;
import com.dreamland.mapper.UserMapper;
import com.dreamland.pojo.Question;
import com.dreamland.pojo.QuestionExample;
import com.dreamland.pojo.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionDTOService {

    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private UserMapper userMapper;

    public PaginationDTO list(Integer page, Integer size) {
        Integer totalCount = (int)questionMapper.countByExample(new QuestionExample());
        Integer questionCount = totalCount % size == 0 ? totalCount / size : totalCount / size + 1;

        if (page < 1)
            page = 1;
        if (page > questionCount)
            page = questionCount;
        Integer offset = 0;
        if(page != 0){
            offset = size * (page - 1);
        }
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(new QuestionExample(), new RowBounds(offset,size));
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        PaginationDTO paginationDTO = new PaginationDTO();
        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);

        paginationDTO.setPagination(totalCount, page, size);
        return paginationDTO;
    }
    public PaginationDTO listByUserId(Integer page, Integer size, Integer userId) {

        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                .andCreatorEqualTo(userId);
        Integer totalCount = (int)questionMapper.countByExample(questionExample);
        Integer questionCount = totalCount % size == 0 ? totalCount / size : totalCount / size + 1;
        if (page < 1)
            page = 1;
        if (page > questionCount)
            page = questionCount;
        Integer offset = 0;
        if(page != 0) {
            offset = size * (page - 1);
        }
        QuestionExample example = new QuestionExample();
        example.createCriteria()
                .andCreatorEqualTo(userId);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size));

        List<QuestionDTO> questionDTOList = new ArrayList<>();
        PaginationDTO paginationDTO = new PaginationDTO();
        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestions(questionDTOList);

        paginationDTO.setPagination(totalCount, page, size);
        return paginationDTO;
    }

    public QuestionDTO getById(Integer id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if(question == null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO);
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void addViewCount(Integer id){
        questionMapper.addViewCount(id);
    }
}
