package com.serein.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.serein.annotation.AuthCheck;
import com.serein.constants.ErrorCode;
import com.serein.constants.ErrorInfo;
import com.serein.constants.UserRole;
import com.serein.exception.BusinessException;
import com.serein.model.AdminPassageQueryPageRequest;
import com.serein.model.AdminUserQueryPageRequest;
import com.serein.model.Request.GetUserByIdListRequest;
import com.serein.model.dto.userDTO.AddUserDTO;
import com.serein.model.vo.PassageVO.AdminPassageVO;
import com.serein.model.vo.UserVO.AdminUserVO;
import com.serein.service.PassageService;
import com.serein.service.UserService;
import com.serein.utils.BaseResponse;
import com.serein.utils.ResultUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:懒大王Smile
 * @Date: 2024/12/2
 * @Time: 18:27
 * @Description:
 */

@RequestMapping("/admin/passage")
@RestController
public class AdminPassageController {

    @Autowired
    PassageService passageService;

    /**
     * 拒绝/驳回文章
     * @param passageId
     * @return
     */
    @AuthCheck(mustRole = UserRole.ADMIN_ROLE)
    @GetMapping("/reject/{passageId}")
    public BaseResponse<Boolean> rejectPassage(@PathVariable String passageId){
        Boolean aBoolean = passageService.rejectPassage(Long.valueOf(passageId));
        return ResultUtil.success(aBoolean);
    }

    /**
     * 审核通过文章
     * @param passageId
     * @return
     */
    @AuthCheck(mustRole = UserRole.ADMIN_ROLE)
    @GetMapping("/publish/{passageId}")
    public BaseResponse<Boolean> publishPassage(@PathVariable String passageId){
        Boolean aBoolean = passageService.publishPassage(Long.valueOf(passageId));
        return ResultUtil.success(aBoolean);
    }

    /**
     *
     * @param adminPassageQueryPageRequest
     * @return
     */
    @AuthCheck(mustRole = UserRole.ADMIN_ROLE)
    @PostMapping("/getPassageList")
    public BaseResponse<Page<List<AdminPassageVO>>> getPassageList(@RequestBody AdminPassageQueryPageRequest adminPassageQueryPageRequest){
        Page<List<AdminPassageVO>> adminPassageVOList = passageService.getPassageList(adminPassageQueryPageRequest);
        return ResultUtil.success(adminPassageVOList);
    }


}
