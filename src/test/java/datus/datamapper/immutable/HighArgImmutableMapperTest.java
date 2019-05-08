package datus.datamapper.immutable;

import datus.datamapper.api.Mapper;
import datus.datamapper.testutil.DetailedPerson;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class HighArgImmutableMapperTest {


    @Test
    public void basicCopyingShouldWorkCorrectly() {
        //why o why java-type inference, why do you hate me so much
        Mapper<DetailedPerson, DetailedPerson> mapper = new ConstructorBuilder8
                <DetailedPerson, String, String, String, String, String, String, String, String, DetailedPerson>
                (DetailedPerson::new)
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
        DetailedPerson result = mapper.convert(source);

        assertThat("Field should have correct value", result.getUniqueId(), is("uniqueId"));
        assertThat("Field should have correct value", result.getSalutation(), is("salutation"));
        assertThat("Field should have correct value", result.getFirstName(), is("firstName"));
        assertThat("Field should have correct value", result.getLastName(), is("lastName"));
        assertThat("Field should have correct value", result.getAddress(), is("address"));
        assertThat("Field should have correct value", result.getAddressExtra(), is("addressExtra"));
        assertThat("Field should have correct value", result.getCity(), is("city"));
        assertThat("Field should have correct value", result.getOccupation(), is("occupation"));
    }

    @Test
    public void simpleUsageShouldWorkCorrectly() {
        Mapper<Object, DetailedPerson> mapper = new ConstructorBuilder8<>(DetailedPerson::new)
                .from(Function.identity()).mapTo(o -> "0")
                .from(Function.identity()).mapTo(o -> "1")
                .from(Function.identity()).mapTo(o -> "2")
                .from(Function.identity()).mapTo(o -> "3")
                .from(Function.identity()).mapTo(o -> "4")
                .from(Function.identity()).mapTo(o -> "5")
                .from(Function.identity()).mapTo(o -> "6")
                .from(Function.identity()).mapTo(o -> "7")
                .build();

        DetailedPerson result = mapper.convert(new Object());

        assertThat("Field should have correct value", result.getUniqueId(), is("0"));
        assertThat("Field should have correct value", result.getSalutation(), is("1"));
        assertThat("Field should have correct value", result.getFirstName(), is("2"));
        assertThat("Field should have correct value", result.getLastName(), is("3"));
        assertThat("Field should have correct value", result.getAddress(), is("4"));
        assertThat("Field should have correct value", result.getAddressExtra(), is("5"));
        assertThat("Field should have correct value", result.getCity(), is("6"));
        assertThat("Field should have correct value", result.getOccupation(), is("7"));
    }

    @Test
    public void logicfulUsageShouldWorkCorrectly() {
        //why o why java-type inference, why do you hate me so much
        Mapper<DetailedPerson, DetailedPerson> mapper = new ConstructorBuilder8
                <DetailedPerson, String, String, String, String, String, String, String, String, DetailedPerson>
                (DetailedPerson::new)
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
        DetailedPerson result = mapper.convert(source);

        assertThat("Field should have correct value", result.getUniqueId(), is("uniqueId1"));
        assertThat("Field should have correct value", result.getSalutation(), is("salutation1"));
        assertThat("Field should have correct value", result.getFirstName(), is("firstName1"));
        assertThat("Field should have correct value", result.getLastName(), is("lastName1"));
        assertThat("Field should have correct value", result.getAddress(), is("address1"));
        assertThat("Field should have correct value", result.getAddressExtra(), is("addressExtra1"));
        assertThat("Field should have correct value", result.getCity(), is("city1"));
        assertThat("Field should have correct value", result.getOccupation(), is("occupation1"));
    }

    private String appendExclamationMark(String input) {
        return input + 1;
    }

}
