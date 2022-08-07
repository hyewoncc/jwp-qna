package qna.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import qna.fixtures.QuestionFixture;
import qna.fixtures.UserFixture;

@TestConstructor(autowireMode = AutowireMode.ALL)
@DataJpaTest
class QuestionRepositoryTest {

    private final UserRepository users;
    private final QuestionRepository questions;
    private final AnswerRepository answers;

    QuestionRepositoryTest(UserRepository users, QuestionRepository questions, AnswerRepository answers) {
        this.users = users;
        this.questions = questions;
        this.answers = answers;
    }

    @DisplayName("삭제되지 않은 모든 Question 조회")
    @Test
    void findByDeletedFalse() {
        User user = users.save(UserFixture.JAVAJIGI.generate());
        Question expectIncluded = questions.save(QuestionFixture.FIRST.generate().writeBy(user));
        questions.save(expectIncluded);

        Question expectNotIncluded = QuestionFixture.SECOND.generate().writeBy(user);
        expectNotIncluded.deleteBy(user);
        questions.save(expectNotIncluded);

        List<Question> actual = questions.findByDeletedFalse();

        assertAll(
                () -> assertThat(actual.contains(expectIncluded)).isTrue(),
                () -> assertThat(actual.contains(expectNotIncluded)).isFalse()
        );
    }

    @DisplayName("id가 일치하고 삭제되지 않은 Question을 조회하고, 값이 존재")
    @Test
    void findByIdAndDeletedFalse_resultExist() {
        User user = users.save(UserFixture.JAVAJIGI.generate());
        Question expect = QuestionFixture.FIRST.generate().writeBy(user);
        Question saved = questions.save(expect);

        Optional<Question> actual = questions.findByIdAndDeletedFalse(saved.getId());

        assertAll(
                () -> assertThat(actual).isPresent(),
                () -> assertThat(actual.get()).isEqualTo(expect)
        );
    }

    @DisplayName("id가 일치하고 삭제되지 않은 Question을 조회하고, 값이 존재하지 않음")
    @Test
    void findByIdAndDeletedFalse_resultDoesNotExist() {
        User user = users.save(UserFixture.JAVAJIGI.generate());
        Question expect = QuestionFixture.FIRST.generate().writeBy(user);
        expect.deleteBy(user);
        Question saved = questions.save(expect);

        Optional<Question> actual = questions.findByIdAndDeletedFalse(saved.getId());

        assertThat(actual).isEmpty();
    }
}
