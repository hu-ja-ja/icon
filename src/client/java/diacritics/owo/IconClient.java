package diacritics.owo;

import java.io.IOException;
import java.util.Map.Entry;
import diacritics.owo.util.IconsHelpers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class IconClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.registerBuiltinResourcePack(Icon.identifier("alternate_icons"),
				FabricLoader.getInstance().getModContainer(Icon.MOD_ID).get(),
				ResourcePackActivationType.NORMAL);

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
				.registerReloadListener(new SimpleSynchronousResourceReloadListener() {
					@Override
					public Identifier getFabricId() {
						return Icon.identifier("icons");
					}

					@Override
					public void reload(ResourceManager manager) {
						boolean found = false;

						for (Entry<Identifier, Resource> entry : manager
								.findResources(IconsHelpers.ICONS, path -> true).entrySet()) {
							if (found) {
								return;
							}

							Identifier identifier = Identifier.of(entry.getKey().getNamespace(),
									IconsHelpers.ICONS
											+ (SharedConstants.getGameVersion().isStable() ? IconsHelpers.STABLE
													: IconsHelpers.SNAPSHOT));
							Resource resource = entry.getValue();

							try {
								IconsHelpers.setIcon(identifier, resource.getPack());
								Icon.LOGGER.info("successfully used icons from resource pack {}",
										resource.getPackId());
								found = true;
							} catch (IOException exception) {
								Icon.LOGGER.warn("failed to get icons from resource pack {}", resource.getPackId(),
										exception);
							}
						}
					}
				});
	}
}
