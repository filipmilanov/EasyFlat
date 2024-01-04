package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.endpoint.dto.ChoreDto;
import at.ac.tuwien.sepr.groupphase.backend.entity.Chore;
import at.ac.tuwien.sepr.groupphase.backend.exception.AuthenticationException;

import java.util.List;

public interface ChoreService {

    ChoreDto createChore(ChoreDto chore) throws AuthenticationException;

    List<Chore> getChores(String searchParams) throws AuthenticationException;

    List<ChoreDto> assignChores() throws AuthenticationException;

    List<Chore> getChoresByUser() throws AuthenticationException;

    List<Chore> deleteChores(List<Long> choreIds);
}
