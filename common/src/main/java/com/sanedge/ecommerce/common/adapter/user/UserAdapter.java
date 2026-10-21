package com.sanedge.ecommerce.common.adapter.user;

import com.sanedge.ecommerce.common.adapter.model.User;
import com.sanedge.ecommerce.common.adapter.model.VerifyPasswordResult;
import com.sanedge.ecommerce.common.adapter.support.AdapterException;
import com.sanedge.ecommerce.common.adapter.support.ProtoTime;
import com.sanedge.ecommerce.common.exception.ResourceNotFoundException;
import com.sanedge.ecommerce.pb.user.ApiResponseUser;
import com.sanedge.ecommerce.pb.user.CreateUserRequest;
import com.sanedge.ecommerce.pb.user.FindByEmailRequest;
import com.sanedge.ecommerce.pb.user.FindByIdUserRequest;
import com.sanedge.ecommerce.pb.user.FindByVerificationCodeRequest;
import com.sanedge.ecommerce.pb.user.UpdateUserIsVerifiedRequest;
import com.sanedge.ecommerce.pb.user.UpdateUserPasswordRequest;
import com.sanedge.ecommerce.pb.user.UserCommandServiceGrpc.UserCommandServiceBlockingStub;
import com.sanedge.ecommerce.pb.user.UserQueryServiceGrpc.UserQueryServiceBlockingStub;
import com.sanedge.ecommerce.pb.user.UserResponse;
import com.sanedge.ecommerce.pb.user.UserResponseWithPassword;
import com.sanedge.ecommerce.pb.user.VerifyPasswordRequest;

public class UserAdapter implements UserPort {

    private final UserQueryServiceBlockingStub query;

    private final UserCommandServiceBlockingStub command;

    public UserAdapter(UserQueryServiceBlockingStub query, UserCommandServiceBlockingStub command) {
        this.query = query;
        this.command = command;
    }

    @Override
    public User findById(int id) {
        var resp = query.findById(FindByIdUserRequest.newBuilder().setId(id).build());
        if (!resp.hasData()) {
            throw new ResourceNotFoundException("User not found: " + id);
        }
        return toModel(resp.getData());
    }

    @Override
    public User findByEmail(String email) {
        var resp = query.findByEmail(FindByEmailRequest.newBuilder().setEmail(email).build());
        if (!resp.hasData()) {
            throw new ResourceNotFoundException("User not found: " + email);
        }
        return toModel(resp.getData());
    }

    @Override
    public User findByEmailAndVerify(String email) {
        return findByEmail(email);
    }

    @Override
    public User findByVerificationCode(String code) {
        var resp = query.findByVerificationCode(FindByVerificationCodeRequest.newBuilder()
                .setVerificationCode(code)
                .build());
        if (!resp.hasData()) {
            throw new ResourceNotFoundException("User not found for verification code");
        }
        return toModel(resp.getData());
    }

    @Override
    public User createUser(RegisterData data) {
        var resp = command.create(CreateUserRequest.newBuilder()
                .setFirstname(data.firstname())
                .setLastname(data.lastname())
                .setEmail(data.email())
                .setPassword(data.password())
                .setConfirmPassword(data.confirmPassword())
                .build());
        return requireData(resp, "Failed to create user");
    }

    @Override
    public VerifyPasswordResult verifyPassword(String email, String password) {
        var resp = command.verifyPassword(VerifyPasswordRequest.newBuilder()
                .setEmail(email)
                .setPassword(password)
                .build());
        return new VerifyPasswordResult(resp.getValid(),
                resp.hasUser() ? toModel(resp.getUser()) : null);
    }

    @Override
    public User updateIsVerified(int userId, boolean isVerified) {
        var resp = command.updateIsVerified(UpdateUserIsVerifiedRequest.newBuilder()
                .setId(userId)
                .setIsVerified(isVerified)
                .build());
        return requireData(resp, "Failed to update user verification: " + userId);
    }

    @Override
    public User updatePassword(int userId, String password) {
        var resp = command.updatePassword(UpdateUserPasswordRequest.newBuilder()
                .setId(userId)
                .setPassword(password)
                .build());
        return requireData(resp, "Failed to update user password: " + userId);
    }

    @Override
    public void deleteUserPermanent(int userId) {
        // Best effort trash dulu (user yang sudah trashed mengembalikan error, abaikan),
        // lalu purge — meniru DeleteUserPermanent di adapter Go.
        try {
            command.trashedUser(FindByIdUserRequest.newBuilder().setId(userId).build());
        } catch (RuntimeException ignored) {
            // abaikan kegagalan trash, lanjut purge
        }
        command.deleteUserPermanent(FindByIdUserRequest.newBuilder().setId(userId).build());
    }

    private static User requireData(ApiResponseUser resp, String message) {
        if (resp == null || !resp.hasData()) {
            throw new AdapterException(message);
        }
        return toModel(resp.getData());
    }

    static User toModel(UserResponse u) {
        if (u == null) {
            return null;
        }
        return new User(
                u.getId(),
                u.getFirstname(),
                u.getLastname(),
                u.getEmail(),
                null,
                ProtoTime.parse(u.getCreatedAt()),
                ProtoTime.parse(u.getUpdatedAt()));
    }

    static User toModel(UserResponseWithPassword u) {
        if (u == null) {
            return null;
        }
        return new User(
                u.getId(),
                u.getFirstname(),
                u.getLastname(),
                u.getEmail(),
                u.getPassword(),
                ProtoTime.parse(u.getCreatedAt()),
                ProtoTime.parse(u.getUpdatedAt()));
    }
}
