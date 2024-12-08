package com.yulcomtechnologies.usersms.events;

import com.yulcomtechnologies.sharedlibrary.exceptions.ResourceNotFoundException;
import com.yulcomtechnologies.usersms.feignClients.NotificationFeignClient;
import com.yulcomtechnologies.usersms.repositories.UserRepository;
import com.yulcomtechnologies.usersms.services.SsoProvider;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountStateChangedListener {
    private final NotificationFeignClient notificationFeignClient;
    private final UserRepository userRepository;
    private final SsoProvider ssoProvider;

    @EventListener
    @Async
    public void handle(AccountStateChanged event) {
        var user = userRepository.findById(event.userId).orElseThrow(
            () -> new ResourceNotFoundException("User not found")
        );

        if (user.getIsActive()) {
            notificationFeignClient.sendNotification(
                user.getEmail(),
                "Validation de compte",
                "Votre compte a été validé avec succès, vous pouvez maintenant vous connecter",
                "",
                ""
            );
        }

        else {
            notificationFeignClient.sendNotification(
                user.getEmail(),
                "Statut de votre compte",
                "Désolé, votre compte n'a pas été approuvé, veuillez nous contacter",
                "",
                ""
            );

            ssoProvider.deleteUser(user.getKeycloakUserId());
        }
    }
}
