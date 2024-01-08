package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreDto;
import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreSearchDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepr.groupphase.backend.exception.ValidationException;

import java.io.IOException;
import java.util.List;

public interface ChoreService {

    ChoreDto createChore(ChoreDto chore) throws AuthenticationException, ValidationException, ConflictException;

    List<Chore> getChores(ChoreSearchDto searchParams) throws AuthenticationException;

    List<ChoreDto> assignChores() throws AuthenticationException;

    List<Chore> getChoresByUser() throws AuthenticationException;

    List<Chore> deleteChores(List<Long> choreIds);

    List<ApplicationUser> getUsers() throws AuthenticationException;

    ApplicationUser updatePoints(Long userId, Integer points);

    byte[] generatePdf(String htmlContent) throws IOException;
}
