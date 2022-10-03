package me.axieum.mcmod.authme.api.gui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Anchor Point Tests")
public class AnchorPointTests
{
    @Test
    @DisplayName("Find the closest anchor point")
    public void getAnchorFromCoords()
    {
        // Top-left = (0, 0) -> (320, 180)
        assertEquals(AnchorPoint.TOP_LEFT, AnchorPoint.getFromCoords(0, 0, 1280, 720));
        assertEquals(AnchorPoint.TOP_LEFT, AnchorPoint.getFromCoords(320, 180, 1280, 720));
        // Top-center (321, 0) -> (960, 180)
        assertEquals(AnchorPoint.TOP_CENTER, AnchorPoint.getFromCoords(321, 0, 1280, 720));
        assertEquals(AnchorPoint.TOP_CENTER, AnchorPoint.getFromCoords(960, 180, 1280, 720));
        // Top-right (961, 0) -> (1280, 180)
        assertEquals(AnchorPoint.TOP_RIGHT, AnchorPoint.getFromCoords(961, 0, 1280, 720));
        assertEquals(AnchorPoint.TOP_RIGHT, AnchorPoint.getFromCoords(1280, 180, 1280, 720));
        // Middle-left (0, 181) -> (320, 540)
        assertEquals(AnchorPoint.MIDDLE_LEFT, AnchorPoint.getFromCoords(0, 181, 1280, 720));
        assertEquals(AnchorPoint.MIDDLE_LEFT, AnchorPoint.getFromCoords(320, 540, 1280, 720));
        // Middle-center (321, 181) -> (960, 540)
        assertEquals(AnchorPoint.MIDDLE_CENTER, AnchorPoint.getFromCoords(321, 181, 1280, 720));
        assertEquals(AnchorPoint.MIDDLE_CENTER, AnchorPoint.getFromCoords(960, 540, 1280, 720));
        // Middle-right (961, 181) -> (1280, 540)
        assertEquals(AnchorPoint.MIDDLE_RIGHT, AnchorPoint.getFromCoords(961, 181, 1280, 720));
        assertEquals(AnchorPoint.MIDDLE_RIGHT, AnchorPoint.getFromCoords(1280, 540, 1280, 720));
        // Bottom-left (0, 541) -> (320, 720)
        assertEquals(AnchorPoint.BOTTOM_LEFT, AnchorPoint.getFromCoords(0, 541, 1280, 720));
        assertEquals(AnchorPoint.BOTTOM_LEFT, AnchorPoint.getFromCoords(320, 720, 1280, 720));
        // Bottom-center (321, 541) -> (960, 720)
        assertEquals(AnchorPoint.BOTTOM_CENTER, AnchorPoint.getFromCoords(321, 541, 1280, 720));
        assertEquals(AnchorPoint.BOTTOM_CENTER, AnchorPoint.getFromCoords(960, 720, 1280, 720));
        // Bottom-right (961, 541) -> (1280, 720)
        assertEquals(AnchorPoint.BOTTOM_RIGHT, AnchorPoint.getFromCoords(961, 541, 1280, 720));
        assertEquals(AnchorPoint.BOTTOM_RIGHT, AnchorPoint.getFromCoords(1280, 720, 1280, 720));
    }
}
