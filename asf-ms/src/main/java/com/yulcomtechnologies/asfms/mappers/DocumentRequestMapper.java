package com.yulcomtechnologies.asfms.mappers;
import com.yulcomtechnologies.asfms.dtos.DocumentRequestDto;
import com.yulcomtechnologies.asfms.entities.DocumentRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@NoArgsConstructor
public class DocumentRequestMapper {


   public DocumentRequest documentRequestToEntity(DocumentRequestDto documentRequestDto) {
        DocumentRequest documentRequestEntity = new DocumentRequest(
                documentRequestDto.getId(),
                documentRequestDto.getStatut_demande(),
                LocalDateTime.now(),
                documentRequestDto.getDateDelivrance(),
                documentRequestDto.getIfu(),
                documentRequestDto.getNes(),
                documentRequestDto.getRaisonSociale(),
                documentRequestDto.getReference()

        );

        return documentRequestEntity;
    }

   public DocumentRequestDto documentEntityToDto(DocumentRequest documentRequestEntity) {
       return new DocumentRequestDto(
               documentRequestEntity.getId(),
               documentRequestEntity.getStatut_demande(),
               documentRequestEntity.toString(),
               documentRequestEntity.getDateDelivrance(),
               documentRequestEntity.getIfu(),
               documentRequestEntity.getNes(),
               documentRequestEntity.getRaisonSociale(),
               documentRequestEntity.getReference()

       );
   }
}
