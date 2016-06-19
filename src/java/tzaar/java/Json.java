package tzaar.java;

import com.google.gson.*;

import java.lang.reflect.Type;

public class Json {

    public static final Gson SERIALIZER = new GsonBuilder()
            .registerTypeAdapter(Position.class, new PositionSerializer())
            .registerTypeAdapter(Piece.class, new PieceSerializer())
            .registerTypeAdapter(Move.class, new MoveSerializer())
            .registerTypeAdapter(Stack.class, new StackSerializer())
            .registerTypeAdapter(Turn.class, new TurnSerializer())
            .registerTypeAdapter(Slot.class, new SlotSerializer())
            .registerTypeAdapter(Board.class, new BoardSerializer())
            .create();

    private Json() {}

    private interface Serializer<T> extends JsonSerializer<T>, JsonDeserializer<T> {}

    private static class PositionSerializer implements Serializer<Position> {

        @Override
        public JsonElement serialize(Position position, Type type, JsonSerializationContext context) {
            JsonArray a = new JsonArray();
            a.add(position.x);
            a.add(position.y);
            return a;
        }

        @Override
        public Position deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonArray a = jsonElement.getAsJsonArray();
            return new Position(
                    a.get(0).getAsInt(),
                    a.get(1).getAsInt());
        }
    }

    private static class PieceSerializer implements Serializer<Piece> {

        @Override
        public JsonElement serialize(Piece piece, Type type, JsonSerializationContext context) {
            JsonArray a = new JsonArray();
            a.add(context.serialize(piece.color));
            a.add(context.serialize(piece.type));
            return a;
        }

        @Override
        public Piece deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonArray a = jsonElement.getAsJsonArray();
            return new Piece(
                    context.deserialize(a.get(0), Color.class),
                    context.deserialize(a.get(1), Piece.Type.class));
        }
    }

    private static class MoveSerializer implements Serializer<Move> {

        @Override
        public JsonElement serialize(Move move, Type type, JsonSerializationContext context) {
            JsonObject o = new JsonObject();
            o.add("moveType", new JsonPrimitive(moveTypeToString(move)));
            if(move instanceof Move.Attack) {
                Move.Attack attackMove = (Move.Attack) move;
                o.add("from", context.serialize(attackMove.from));
                o.add("to", context.serialize(attackMove.to));
            } else if (move instanceof  Move.Stack) {
                Move.Stack stackMove = (Move.Stack) move;
                o.add("from", context.serialize(stackMove.from));
                o.add("to", context.serialize(stackMove.to));
            }
            return o;
        }

        @Override
        public Move deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject o = jsonElement.getAsJsonObject();
            String moveType = o.get("moveType").getAsString();
            if(moveType.equals("pass")) {
                return Move.Pass;
            }
            if(moveType.equals("attack")) {
                return new Move.Attack(
                        context.deserialize(o.get("from"), Position.class),
                        context.deserialize(o.get("to"), Position.class));
            }
            if(moveType.equals("stack")) {
                return new Move.Stack(
                        context.deserialize(o.get("from"), Position.class),
                        context.deserialize(o.get("to"), Position.class));
            }

            throw new JsonParseException("Unknown moveType: " + moveType);
        }

        private String moveTypeToString(Move move) {
            if(move == Move.Pass) {
                return "pass";
            } else if(move instanceof Move.Attack) {
                return "attack";
            } else {
                return "stack";
            }
        }
    }

    private static class StackSerializer implements Serializer<Stack> {

        @Override
        public JsonElement serialize(Stack stack, Type type, JsonSerializationContext context) {
            return context.serialize(stack.pieces);
        }

        @Override
        public Stack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            return new Stack(context.deserialize(jsonElement, Piece[].class));
        }
    }

    private static class TurnSerializer implements Serializer<Turn> {


        @Override
        public JsonElement serialize(Turn turn, Type type, JsonSerializationContext context) {
            JsonArray a = new JsonArray();
            a.add(context.serialize(turn.firstMove));
            a.add(context.serialize(turn.secondMove));
            return a;
        }

        @Override
        public Turn deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonArray a = jsonElement.getAsJsonArray();
            return new Turn(
                    context.deserialize(a, Move.class),
                    context.deserialize(a, Move.class));
        }

    }

    private static class SlotSerializer implements Serializer<Slot> {

        @Override
        public JsonElement serialize(Slot slot, Type type, JsonSerializationContext context) {
            if(slot.isEmpty()) {
                return new JsonPrimitive("empty");
            }
            if(slot.isNothing()) {
                return new JsonPrimitive("nothing");
            }

            return context.serialize(((Slot.Stack)slot).stack);
        }

        @Override
        public Slot deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            if(jsonElement.isJsonPrimitive()) {
                String slot = jsonElement.getAsString();
                if(slot.equals("empty")) {
                    return Slot.Empty;
                }
                if(slot.equals("nothing")) {
                    return Slot.Nothing;
                }
                throw new JsonParseException("Unknown slot type " + slot);
            }

            return new Slot.Stack(context.deserialize(jsonElement, Stack.class));
        }
    }

    private static class BoardSerializer implements Serializer<Board> {

        @Override
        public JsonElement serialize(Board board, Type type, JsonSerializationContext context) {
            return context.serialize(board.slots);
        }

        @Override
        public Board deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            return new Board(context.deserialize(jsonElement, Slot[][].class));
        }
    }
}
