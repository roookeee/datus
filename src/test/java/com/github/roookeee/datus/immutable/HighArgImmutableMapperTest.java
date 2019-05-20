package com.github.roookeee.datus.immutable;

import com.github.roookeee.datus.api.Datus;
import com.github.roookeee.datus.api.Mapper;
import com.github.roookeee.datus.testutil.DetailedPerson;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class HighArgImmutableMapperTest {


    @Test
    public void basicCopyingShouldWorkCorrectly() {
        //given
        //why o why java-type inference, why do you hate me so much
        Mapper<DetailedPerson, DetailedPerson> mapper = Datus.forTypes(DetailedPerson.class, DetailedPerson.class)
                .immutable(DetailedPerson::new)
                .take(DetailedPerson::getUniqueId)
                .take(DetailedPerson::getSalutation)
                .take(DetailedPerson::getFirstName)
                .take(DetailedPerson::getLastName)
                .take(DetailedPerson::getAddress)
                .take(DetailedPerson::getAddressExtra)
                .take(DetailedPerson::getCity)
                .take(DetailedPerson::getOccupation)
                .build();

        DetailedPerson source = new DetailedPerson(
                "uniqueId",
                "salutation",
                "firstName",
                "lastName",
                "address",
                "addressExtra",
                "city",
                "occupation"
        );

        //when
        DetailedPerson result = mapper.convert(source);

        //then
        assertThat(result.getUniqueId()).isEqualTo("uniqueId");
        assertThat(result.getSalutation()).isEqualTo("salutation");
        assertThat(result.getFirstName()).isEqualTo("firstName");
        assertThat(result.getLastName()).isEqualTo("lastName");
        assertThat(result.getAddress()).isEqualTo("address");
        assertThat(result.getAddressExtra()).isEqualTo("addressExtra");
        assertThat(result.getCity()).isEqualTo("city");
        assertThat(result.getOccupation()).isEqualTo("occupation");
    }

    @Test
    public void simpleUsageShouldWorkCorrectly() {
        //given
        Mapper<Object, DetailedPerson> mapper = Datus.forTypes(Object.class, DetailedPerson.class)
                .immutable(DetailedPerson::new)
                .from(Function.identity()).mapTo(o -> "0")
                .from(Function.identity()).mapTo(o -> "1")
                .from(Function.identity()).mapTo(o -> "2")
                .from(Function.identity()).mapTo(o -> "3")
                .from(Function.identity()).mapTo(o -> "4")
                .from(Function.identity()).mapTo(o -> "5")
                .from(Function.identity()).mapTo(o -> "6")
                .from(Function.identity()).mapTo(o -> "7")
                .build();

        //when
        DetailedPerson result = mapper.convert(new Object());

        //then
        assertThat(result.getUniqueId()).isEqualTo("0");
        assertThat(result.getSalutation()).isEqualTo("1");
        assertThat(result.getFirstName()).isEqualTo("2");
        assertThat(result.getLastName()).isEqualTo("3");
        assertThat(result.getAddress()).isEqualTo("4");
        assertThat(result.getAddressExtra()).isEqualTo("5");
        assertThat(result.getCity()).isEqualTo("6");
        assertThat(result.getOccupation()).isEqualTo("7");
    }

    @Test
    public void logicfulUsageShouldWorkCorrectly() {
        //given
        //why o why java-type inference, why do you hate me so much
        Mapper<DetailedPerson, DetailedPerson> mapper = Datus.forTypes(DetailedPerson.class, DetailedPerson.class)
                .immutable(DetailedPerson::new)
                .from(DetailedPerson::getUniqueId).mapTo(this::appendExclamationMark)
                .from(DetailedPerson::getSalutation).mapTo(this::appendExclamationMark)
                .from(DetailedPerson::getFirstName).mapTo(this::appendExclamationMark)
                .from(DetailedPerson::getLastName).mapTo(this::appendExclamationMark)
                .from(DetailedPerson::getAddress).mapTo(this::appendExclamationMark)
                .from(DetailedPerson::getAddressExtra).mapTo(this::appendExclamationMark)
                .from(DetailedPerson::getCity).mapTo(this::appendExclamationMark)
                .from(DetailedPerson::getOccupation).mapTo(this::appendExclamationMark)
                .build();

        DetailedPerson source = new DetailedPerson(
                "uniqueId",
                "salutation",
                "firstName",
                "lastName",
                "address",
                "addressExtra",
                "city",
                "occupation"
        );

        //when
        DetailedPerson result = mapper.convert(source);

        //then
        assertThat(result.getUniqueId()).isEqualTo("uniqueId!");
        assertThat(result.getSalutation()).isEqualTo("salutation!");
        assertThat(result.getFirstName()).isEqualTo("firstName!");
        assertThat(result.getLastName()).isEqualTo("lastName!");
        assertThat(result.getAddress()).isEqualTo("address!");
        assertThat(result.getAddressExtra()).isEqualTo("addressExtra!");
        assertThat(result.getCity()).isEqualTo("city!");
        assertThat(result.getOccupation()).isEqualTo("occupation!");
    }

    private String appendExclamationMark(String input) {
        return input + "!";
    }

}
