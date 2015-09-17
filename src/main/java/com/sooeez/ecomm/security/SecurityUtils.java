package com.sooeez.ecomm.security;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public final class SecurityUtils {
	
	/**
	 * 得到当前的登入者
	 * @return 返回用户名
	 */
    public static String getCurrentLogin() {
		// 从【安全持有者】中取出【安全上下文】
		SecurityContext securityContext = SecurityContextHolder.getContext();
		// 从【安全上下文】中得到【认证】
		Authentication authentication = securityContext.getAuthentication();
		UserDetails springSecurityUser = null;
		String userName = null;
		if (authentication != null) { // 如果【认证】不等于空
			if (authentication.getPrincipal() instanceof UserDetails) { // 判断【认证】中的【当事人】是否为【UserDetails】接口的实例
				springSecurityUser = (UserDetails) authentication.getPrincipal(); // 如果是，强转类型为【UserDetails】
				userName = springSecurityUser.getUsername(); // 从中得到用户名
			} else if (authentication.getPrincipal() instanceof String) { // 判断【认证】中的【当事人】是否为【String】类的实例
				userName = (String) authentication.getPrincipal(); // 如果是，直接拿到这个字符串
			}
		}
		return userName;
    }

    /**
     * 检查一个用户是否被认证
     * @return true if the user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        // 得到当事人中的所有权限
        Collection<? extends GrantedAuthority> authorities = securityContext.getAuthentication().getAuthorities();
        if (authorities != null) {
            for (GrantedAuthority authority : authorities) { 
            	// 如果当事人中的权限有一个为匿名的角色，那么证明当事人没有被认证过，返回false
                if (authority.getAuthority().equals(AuthoritiesConstants.ANONYMOUS)) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 检查一个用户是否为传入的权限角色，这些角色应该都在spring security的维护范围内
     */
    public static boolean isUserInRole(String role) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if(authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
                return springSecurityUser.getAuthorities().contains(new SimpleGrantedAuthority(role));
            }
        }
        return false;
    }

}
