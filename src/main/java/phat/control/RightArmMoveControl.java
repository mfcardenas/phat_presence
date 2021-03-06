/*
 * Copyright (C) 2014 Pablo Campillo-Sanchez <pabcampi@ucm.es>
 *
 * This software has been developed as part of the 
 * SociAAL project directed by Jorge J. Gomez Sanz
 * (http://grasia.fdi.ucm.es/sociaal)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package phat.control;

import com.jme3.animation.Bone;
import com.jme3.animation.SkeletonControl;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * It generate a trembling on character right hand.
 *
 * Depends on <b>SkeletonControl</b>
 *
 * @author ucm
 */
public class RightArmMoveControl extends AbstractControl {
    
    private static final Logger logger = Logger.getLogger(RightArmMoveControl.class.getName());
    
    SkeletonControl skeletonControl;
    Bone hand;
    Vector3f position = new Vector3f();
    Quaternion rotation = new Quaternion();
    
    float[] angles = new float[3];
   
    int index = 2;
    
    float minAngle = FastMath.HALF_PI;
    float maxAngle = 0;
    float angular = FastMath.PI;
    
    boolean sw_up = true;
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial != null) {
            skeletonControl = spatial.getControl(SkeletonControl.class);
            hand = skeletonControl.getSkeleton().getBone("RightForeArm");
            rotation = new Quaternion();
        } else {
            resetHand();
            skeletonControl = null;
            rotation = null;
        }
    }

    private void resetHand() {
        if (hand != null) {
            setUserControlFrom(hand, true);
            hand.getCombinedTransform(position, rotation);
            angles[0] = 0;
            angles[1] = 0;
            angles[2] = 0;
            rotation.fromAngles(angles);
            hand.setUserTransforms(position, rotation, Vector3f.UNIT_XYZ);
            updateBonePositions(hand);
        }
    }
    
    @Override
    protected void controlUpdate(float fps) {
        if (hand != null) {
            setUserControlFrom(hand, true);
            hand.getCombinedTransform(position, rotation);
            updateRotation(rotation, fps);
            hand.setUserTransforms(position, rotation, Vector3f.UNIT_XYZ);
            updateBonePositions(hand);
        }
    }

    private void updateRotation(Quaternion rotation, float tpf) {
        rotation.toAngles(angles);
        float angle = angles[index];

        if(angle > minAngle && sw_up){
            angle -= angular * tpf;
            //logger.info("Rotation Up  --------Angle value: " + angle + ", tpf: " + tpf);
        }else{
            //logger.info("Rotation Stop-Up-----Angle value: " + angle + ", tpf: " + tpf);
            sw_up = false;
        }
        
        if(!sw_up && angle < maxAngle){
            angle += angular * tpf;
            //logger.info("Rotation Donw--------Angle value: " + angle + ", tpf: " + tpf);
        }else{

            //logger.info("Rotation Stop-Down-----Angle value: " + angle + ", tpf: " + tpf);
        }
        
        angles[index] = angle;
        //logger.info("" + angles[0] + "," + angles[1] + "," + angles[2] +"," + tpf + "," + "rigth");
        rotation.fromAngles(angles);
    }
    
    private void updateBonePositions(Bone bone) {
        Transform t = new Transform();
        for (Bone b : bone.getChildren()) {
            t = b.getCombinedTransform(bone.getModelSpacePosition(), bone.getModelSpaceRotation());
            b.setUserTransformsWorld(t.getTranslation(), b.getModelSpaceRotation());
            updateBonePositions(b);
        }
    }

    private void setUserControlFrom(Bone bone, boolean userControl) {
        bone.setUserControl(userControl);
        for (Bone b : bone.getChildren()) {
            setUserControlFrom(b, userControl);
        }
    }

    public float getMinAngle() {
        return minAngle;
    }

    public void setMinAngle(float minAngle) {
        this.minAngle = minAngle;
    }

    public float getMaxAngle() {
        return maxAngle;
    }

    public void setMaxAngle(float maxAngle) {
        this.maxAngle = maxAngle;
    }

    public float getAngular() {
        return angular;
    }

    public void setAngular(float angular) {
        this.angular = angular;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        
    }

    @Override
    public Control cloneForSpatial(Spatial sptl) {
        RightArmMoveControl control = new RightArmMoveControl();
        control.setSpatial(sptl);
        control.setAngular(angular);
        control.setMaxAngle(maxAngle);
        control.setMinAngle(minAngle);
        return control;
    }
    
    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(angular, "angular", angular);
        oc.write(maxAngle, "maxAngle", maxAngle);
        oc.write(minAngle, "minAngle", minAngle);
        
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        angular = ic.readFloat("angular", angular);
        maxAngle = ic.readFloat("maxAngle", maxAngle);
        minAngle = ic.readFloat("minAngle", minAngle);
    }
}
