package com.loohp.interactivechatdiscordsrvaddon.resources.textures;

import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class TextureResource {

    public static final String PNG_MCMETA_SUFFIX = ".png.mcmeta";
    public static final String MCMETA_SUFFIX = ".mcmeta";

    private TextureManager manager;
    private String resourceKey;
    private ResourcePackFile file;
    private boolean isTexture;
    private Reference<BufferedImage> texture;

    public TextureResource(TextureManager manager, String resourceKey, ResourcePackFile file, boolean isTexture) {
        this.manager = manager;
        this.resourceKey = resourceKey;
        this.file = file;
        this.isTexture = isTexture;
        this.texture = null;
    }

    protected TextureResource(TextureManager manager, String resourceKey, ResourcePackFile file, BufferedImage image) {
        this.manager = manager;
        this.resourceKey = resourceKey;
        this.file = file;
        this.isTexture = true;
        this.texture = new WeakReference<>(image);
    }

    public TextureResource(TextureManager manager, String resourceKey, ResourcePackFile file) {
        this(manager, resourceKey, file, false);
    }

    private BufferedImage loadImage() {
        if (!isTexture) {
            throw new IllegalStateException(resourceKey + " is not a texture!");
        }
        BufferedImage image;
        if (texture != null && (image = texture.get()) != null) {
            return image;
        }
        try (InputStream inputStream = file.getInputStream()) {
            image = ImageIO.read(inputStream);
            this.texture = new WeakReference<>(image);
            return image;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public boolean isTexture() {
        return isTexture;
    }

    public BufferedImage getTexture(int w, int h) {
        return ImageUtils.toCompatibleImage(ImageUtils.resizeImageAbs(loadImage(), w, h));
    }

    public BufferedImage getTexture() {
        loadImage();
        return ImageUtils.toCompatibleImage(ImageUtils.copyImage(loadImage()));
    }

    public boolean hasFile() {
        return file != null;
    }

    public ResourcePackFile getFile() {
        return file;
    }

    public boolean isTextureMeta() {
        return false;
    }

    public boolean hasTextureMeta() {
        return getTextureMeta() != null;
    }

    public TextureMeta getTextureMeta() {
        if (resourceKey == null || manager == null) {
            return null;
        }
        TextureResource meta = manager.getTexture(resourceKey.contains(".") ? resourceKey + MCMETA_SUFFIX : resourceKey + PNG_MCMETA_SUFFIX, false);
        if (meta != null && meta.isTextureMeta()) {
            return (TextureMeta) meta;
        }
        return null;
    }

}
