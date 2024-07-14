package gift.service.wish;

import gift.domain.member.Member;
import gift.domain.member.MemberRepository;
import gift.domain.product.Product;
import gift.domain.product.ProductReposiotory;
import gift.domain.wish.Wish;
import gift.domain.wish.WishRepository;
import gift.mapper.WishMapper;
import gift.web.dto.WishDto;
import gift.web.exception.MemberNotFoundException;
import gift.web.exception.ProductNotFoundException;
import gift.web.exception.WishProductNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WishService {
    private final WishRepository wishRepository;
    private final MemberRepository memberRepository;
    private final ProductReposiotory productReposiotory;
    private final WishMapper wishMapper;

    public WishService(WishRepository wishRepository, WishMapper wishMapper, MemberRepository memberRepository, ProductReposiotory productReposiotory) {
        this.wishRepository = wishRepository;
        this.wishMapper = wishMapper;
        this.memberRepository = memberRepository;
        this.productReposiotory = productReposiotory;
    }

    public List<WishDto> getWishes() {
        return wishRepository.findAll()
            .stream()
            .map(wishMapper::toDto)
            .toList();
    }

    public List<WishDto> getWishesByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new MemberNotFoundException("멤버가 엄슴다"));

        return wishRepository.findByMember_Id(member.getId())
            .stream()
            .map(wishMapper::toDto)
            .toList();
    }

    public WishDto createWish(String email, WishDto wishDto) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new MemberNotFoundException("멤버가 엄슴다"));

        Product product = productReposiotory.findById(wishDto.productId())
            .orElseThrow(() -> new ProductNotFoundException("제품이 없슴다."));

        Wish wish = wishRepository.save(wishMapper.toEntity(wishDto, member, product));
        return wishMapper.toDto(wish);
    }

    public WishDto updateWish(String email, WishDto wishDto) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new MemberNotFoundException("멤버가 엄슴다"));

        Product product = productReposiotory.findById(wishDto.productId())
            .orElseThrow(() -> new ProductNotFoundException("제품이 없슴다."));

        Wish wish = wishRepository.findByMember_IdAndProduct_Id(member.getId(), wishDto.productId())
                .orElseThrow(() -> new WishProductNotFoundException("위시 제품이 없슴다."));

        wish.updateWish(wishDto.count());

        return wishMapper.toDto(wishRepository.save(wish));
    }

    public void deleteWish(String email, Long productId) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new MemberNotFoundException("멤버가 엄슴다"));

        Product product = productReposiotory.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("제품이 없슴다."));

        Wish wish = wishRepository.findByMember_IdAndProduct_Id(member.getId(), productId)
            .orElseThrow(() -> new WishProductNotFoundException("위시 제품이 없슴다."));

        wishRepository.delete(wish);
    }
}
