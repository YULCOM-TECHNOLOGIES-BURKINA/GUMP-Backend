package com.yulcomtechnologies.usersms.events;

import com.yulcomtechnologies.sharedlibrary.exceptions.ResourceNotFoundException;
import com.yulcomtechnologies.usersms.feignClients.NotificationFeignClient;
import com.yulcomtechnologies.usersms.repositories.CompanyRepository;
import com.yulcomtechnologies.usersms.repositories.FileRepository;
import com.yulcomtechnologies.usersms.repositories.UserRepository;
import com.yulcomtechnologies.usersms.services.SsoProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class AccountStateChangedListener {
    private final NotificationFeignClient notificationFeignClient;
    private final UserRepository userRepository;
    private final SsoProvider ssoProvider;
    private final CompanyRepository companyRepository;
    private final FileRepository fileRepository;


    @EventListener
    public void handle(AccountStateChanged event) {
        log.info("Handling account state changed event");

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
            log.info("User account not approved, deleting user and company");
            notificationFeignClient.sendNotification(
                user.getEmail(),
                "Statut de votre compte",
                "Désolé, votre compte n'a pas été approuvé, veuillez nous contacter",
                "",
                ""
            );

            var company = user.getCompany();
            companyRepository.delete(company);

            userRepository.delete(user);

            if (company.getEnterpriseStatut() != null) {
                fileRepository.delete(company.getEnterpriseStatut());
            }

            if (company.getIdDocument() != null) {
                fileRepository.delete(company.getIdDocument());
            }

            ssoProvider.deleteUser(user.getKeycloakUserId());
        }
    }
}
