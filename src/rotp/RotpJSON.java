package rotp;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import rotp.model.galaxy.Galaxy;
import rotp.model.galaxy.Ships;
import rotp.model.game.GameSession;
import rotp.model.game.GameStatus;
import rotp.model.game.MOO1GameOptions;

import java.io.IOException;
import java.lang.reflect.Field;

public class RotpJSON {

    public static ObjectMapper objectMapper = new ObjectMapper();

    static {
        SimpleModule simpleModule = new SimpleModule("ROTP",
                new Version(1, 0, 0, null));
        simpleModule.addSerializer(new RotpJSON.GameSessionSerializer());
        simpleModule.addSerializer(new RotpJSON.GameOptionsSerializer());
        simpleModule.addSerializer(new RotpJSON.ShipsSerializer());
        simpleModule.addSerializer(new RotpJSON.GameStatusSerializer());
        // TODO: Galaxy and others
        objectMapper.registerModule(simpleModule);
    }

    public static class GameSessionSerializer extends SerializerBase<GameSession> {
        protected GameSessionSerializer() {
            super(GameSession.class);
        }

        @Override
        public void serialize(GameSession gameSession, JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider) throws IOException, JsonProcessingException {

            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField("options", gameSession.options());
            jsonGenerator.writeObjectField("galaxy", gameSession.galaxy());
            jsonGenerator.writeObjectField("status", gameSession.status());
            jsonGenerator.writeNumberField("id", gameSession.id());
            jsonGenerator.writeNumberField("id", gameSession.id());

            jsonGenerator.writeObjectField("governorOptions", gameSession.getGovernorOptions());
            jsonGenerator.writeObjectField("governorOptions2", gameSession.getGovernorOptions2());
            jsonGenerator.writeEndObject();
        }
    }


    public static class GameOptionsSerializer extends SerializerBase<MOO1GameOptions> {
        protected GameOptionsSerializer() {
            super(MOO1GameOptions.class);
        }

        @Override
        public void serialize(MOO1GameOptions options, JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider) throws IOException, JsonProcessingException {

            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField("opponentRaces", options.selectedOpponentRaces());
            jsonGenerator.writeObjectField("colors", options.possibleColors());
            jsonGenerator.writeObjectField("empireColors", getField("empireColors", options));
            jsonGenerator.writeObjectField("player", options.selectedPlayer());
            jsonGenerator.writeStringField("selectedGalaxySize", options.selectedGalaxySize());
            jsonGenerator.writeStringField("selectedGalaxyShape", options.selectedGalaxyShape());
            jsonGenerator.writeStringField("selectedGameDifficulty", options.selectedGameDifficulty());
            jsonGenerator.writeNumberField("selectedNumberOpponents", options.selectedNumberOpponents());
            jsonGenerator.writeBooleanField("communityAI", options.communityAI());
            jsonGenerator.writeBooleanField("disableRandomEvents", options.disableRandomEvents());
            jsonGenerator.writeEndObject();
        }
    }

    public static class GalaxySerializer extends SerializerBase<Galaxy> {
        protected GalaxySerializer() {
            super(Galaxy.class);
        }

        @Override
        public void serialize(Galaxy galaxy, JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider) throws IOException, JsonProcessingException {

//            jsonGenerator.writeStartObject();
//            jsonGenerator.writeObjectField("opponentRaces", options.selectedOpponentRaces());
//            jsonGenerator.writeObjectField("colors", options.possibleColors());
//            jsonGenerator.writeObjectField("empireColors", getField("empireColors", options));
//            jsonGenerator.writeObjectField("player", options.selectedPlayer());
//            jsonGenerator.writeStringField("selectedGalaxySize", options.selectedGalaxySize());
//            jsonGenerator.writeStringField("selectedGalaxyShape", options.selectedGalaxyShape());
//            jsonGenerator.writeStringField("selectedGameDifficulty", options.selectedGameDifficulty());
//            jsonGenerator.writeNumberField("selectedNumberOpponents", options.selectedNumberOpponents());
//            jsonGenerator.writeBooleanField("communityAI", options.communityAI());
//            jsonGenerator.writeBooleanField("disableRandomEvents", options.disableRandomEvents());
//            jsonGenerator.writeEndObject();
        }
    }

    public static class ShipsSerializer extends SerializerBase<Ships> {
        protected ShipsSerializer() {
            super(Ships.class);
        }

        @Override
        public void serialize(Ships ships, JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider) throws IOException, JsonProcessingException {

            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField("options", getField("allFleets", ships));
            jsonGenerator.writeEndObject();
        }
    }

    public static class GameStatusSerializer extends SerializerBase<GameStatus> {
        protected GameStatusSerializer() {
            super(GameStatus.class);
        }

        @Override
        public void serialize(GameStatus gameStatus, JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider) throws IOException, JsonProcessingException {

            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField("status", getField("status", gameStatus));
            jsonGenerator.writeEndObject();
        }
    }

    // retrieve a field
    public static Object getStaticField(Class<?> cls, String field) {
        return getField(cls, field, null);
    }

    public static Object getField(String field, Object o) {
        return getField(o.getClass(), field, o);
    }

    public static Object getField(Class<?> cls, String field, Object o) {
        try {
            Field f = cls.getDeclaredField(field);
            f.setAccessible(true);
            return f.get(o);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static void setStaticField(Class<?> cls, String field, Object value) {
        setField(cls, field, null, value);
    }

    public static void setField(String field, Object object, Object value) {
        setField(object.getClass(), field, null, value);
    }

    public static void setField(Class<?> cls, String field, Object o, Object value) {
        try {
            Field f = cls.getDeclaredField(field);
            f.setAccessible(true);
            f.set(o, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
