package com.emakas.userService.oauth;

import com.emakas.userService.repository.BaseRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface UserTokenRepository extends BaseRepository<UserToken, UUID> {
}
