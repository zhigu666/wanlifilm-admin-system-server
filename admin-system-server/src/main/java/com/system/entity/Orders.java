package com.system.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.system.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author Byterain
 * @since 2023-05-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_orders")
public class Orders extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableField("uid")
    private Long uid;

    /**
     * 座位号
     */
    @TableField("seats")
    private String seats;

    @TableField("price")
    private BigDecimal price;

    /**
     * 支付时间
     */
//    @TableField("created")
//    private LocalDateTime created;

    @TableField("id")
    private Long id;

    @TableField("username")
    private String username;

    @TableField("name")
    private String name;

    @TableField("releasetime")
    private LocalDateTime releasetime;


    @TableField("endtime")
    private LocalDateTime endtime;
}
