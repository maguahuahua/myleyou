package leyou.com.auth.client;

import com.leyou.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author chenxm
 * @date 2020/7/20 - 16:39
 */
@FeignClient(value = "user-service")
public interface UserClient extends UserApi {
}