package jsaper.coordlist;

import static net.minecraft.server.command.CommandManager.*;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CoordListClient implements ClientModInitializer {
	static String blockPosToPrettyString(BlockPos pos){
		return "x: %d y: %d z: %d".formatted(pos.getX(), pos.getY(), pos.getZ());
	}
	@Override
	public void onInitializeClient() {
		File coordList = new File("coordsList.txt");
		try {
			if(coordList.createNewFile()) {
				System.out.println("File created:" + coordList.getName());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("foo")
				.executes(context -> {
					PlayerEntity player = context.getSource().getPlayer();
					try {
						Scanner scanner = new Scanner(coordList);
						while (scanner.hasNextLine()) {
							String fileContents = scanner.nextLine();
							context.getSource().sendFeedback(() -> Text.literal(fileContents), false);
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					context.getSource().sendFeedback(() -> Text.literal(blockPosToPrettyString(player.getBlockPos())),false);
					return 1;
				}).then(CommandManager.argument("title", MessageArgumentType.message()).executes((context -> {
					PlayerEntity player = context.getSource().getPlayer();
					MessageArgumentType.getSignedMessage(context,"message",(message) -> {
						BlockPos blockPos = player.getBlockPos();
						try {
							FileWriter writer = new FileWriter("coordList.txt",true);
							writer.write("test");
							writer.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					});

					return 1;
				})))));
	}
}