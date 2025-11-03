package xyz.shurlin.sprigserver.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author shurlin
 * @since 2025-11-03
 */
@Getter
@Setter
@ToString
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;

    private String password;
}
