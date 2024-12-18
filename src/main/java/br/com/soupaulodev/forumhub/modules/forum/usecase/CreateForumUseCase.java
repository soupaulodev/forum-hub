package br.com.soupaulodev.forumhub.modules.forum.usecase;

import br.com.soupaulodev.forumhub.modules.exception.usecase.ResourceAlreadyExistsException;
import br.com.soupaulodev.forumhub.modules.exception.usecase.ResourceNotFoundException;
import br.com.soupaulodev.forumhub.modules.forum.controller.dto.ForumCreateRequestDTO;
import br.com.soupaulodev.forumhub.modules.forum.controller.dto.ForumResponseDTO;
import br.com.soupaulodev.forumhub.modules.forum.entity.ForumEntity;
import br.com.soupaulodev.forumhub.modules.forum.mapper.ForumMapper;
import br.com.soupaulodev.forumhub.modules.forum.repository.ForumRepository;
import br.com.soupaulodev.forumhub.modules.user.entity.UserEntity;
import br.com.soupaulodev.forumhub.modules.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Use case for creating a new forum.
 */
@Service
public class CreateForumUseCase {

    private final ForumRepository forumRepository;
    private final UserRepository userRepository;

    /**
     * Constructs a new CreateForumUsecase with the specified repositories.
     *
     * @param forumRepository the repository for managing forums
     * @param userRepository  the repository for managing users
     */
    public CreateForumUseCase(ForumRepository forumRepository,
                              UserRepository userRepository) {
        this.forumRepository = forumRepository;
        this.userRepository = userRepository;
    }

    /**
     * Executes the use case to create a new forum.
     *
     * @param requestDTO the data transfer object containing the forum creation data
     * @return the response data transfer {@link ForumResponseDTO} object containing the created forum data
     * @throws ResourceNotFoundException if the user or forum does not exist
     */
    @Transactional
    public ForumResponseDTO execute(ForumCreateRequestDTO requestDTO) {

        UserEntity user = userRepository.findById(UUID.fromString(requestDTO.ownerId()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (forumRepository.existsByName(requestDTO.name()).equals(true)) {
            throw new ResourceAlreadyExistsException("Forum already exists.");
        }

        ForumEntity forum = ForumMapper.toEntity(requestDTO, user);
        forum.addParticipant(user);
        user.addOwnedForum(forum);

        userRepository.save(user);
        return ForumMapper.toResponseDTO(forum);
    }
}
