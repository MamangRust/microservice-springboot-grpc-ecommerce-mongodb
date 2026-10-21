package com.sanedge.ecommerce.gateway.security;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sanedge.ecommerce.pb.role.FindByIdUserRoleRequest;
import com.sanedge.ecommerce.pb.role.RoleQueryServiceGrpc;
import com.sanedge.ecommerce.pb.role.RoleResponse;
import com.sanedge.ecommerce.pb.user.ApiResponseUserWithPassword;
import com.sanedge.ecommerce.pb.user.FindByEmailRequest;
import com.sanedge.ecommerce.pb.user.UserQueryServiceGrpc;

import lombok.RequiredArgsConstructor;

/**
 * Gateway-side {@link UserDetailsService}. The gateway owns no database, so the
 * user and its authorities are loaded from the user and role services over gRPC.
 * The JWT subject is the user's email.
 */
@Service
@RequiredArgsConstructor
public class UserDetailImplService implements UserDetailsService {

    private final UserQueryServiceGrpc.UserQueryServiceBlockingStub userQueryStub;
    private final RoleQueryServiceGrpc.RoleQueryServiceBlockingStub roleQueryStub;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ApiResponseUserWithPassword response;
        try {
            response = userQueryStub.findByEmail(FindByEmailRequest.newBuilder()
                    .setEmail(username)
                    .build());
        } catch (Exception e) {
            throw new UsernameNotFoundException("User Not Found with email: " + username, e);
        }

        if (!response.hasData() || response.getData().getId() == 0) {
            throw new UsernameNotFoundException("User Not Found with email: " + username);
        }

        var user = response.getData();

        List<GrantedAuthority> authorities = roleQueryStub
                .findByUserId(FindByIdUserRoleRequest.newBuilder().setUserId(user.getId()).build())
                .getDataList().stream()
                .map(RoleResponse::getName)
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();

        return new UserDetailsImpl((long) user.getId(), user.getEmail(), user.getEmail(),
                user.getPassword(), authorities);
    }
}
