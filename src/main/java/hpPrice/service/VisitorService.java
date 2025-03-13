package hpPrice.service;

import hpPrice.domain.common.Visitor;
import hpPrice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitorService {

    private final PostRepository postRepository;

    @Async
    public void recordVisitor(String ipAddress, String requestUrl) {
        CompletableFuture.runAsync(() -> {
            postRepository.newVisitor(Visitor.newVisitor(ipAddress, requestUrl));
        });
    }
}
