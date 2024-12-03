package com.serein.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.serein.mapper.LetterMapper;
import com.serein.model.entity.Letter;
import com.serein.service.LetterService;
import org.springframework.stereotype.Service;

/**
 * @author 懒大王Smile
 * @description 针对表【letter(私信表)】的数据库操作Service实现
 * @createDate 2024-09-12 22:19:13
 */
@Service
public class LetterServiceImpl extends ServiceImpl<LetterMapper, Letter>
    implements LetterService {

}




